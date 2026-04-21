package com.seatunnel.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.seatunnel.orchestrator.annotations.ActionLog;
import com.seatunnel.orchestrator.config.OrchestrationProperties;
import com.seatunnel.orchestrator.enums.JobMode;
import com.seatunnel.orchestrator.enums.JobStatus;
import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.enums.SourcePlugin;
import com.seatunnel.orchestrator.exception.ApiException;
import com.seatunnel.orchestrator.model.*;
import com.seatunnel.orchestrator.projection.JobProjection;
import com.seatunnel.orchestrator.repository.JobRepository;
import com.seatunnel.orchestrator.util.CommonUtil;
import com.seatunnel.orchestrator.validator.PipelineValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.seatunnel.orchestrator.enums.JobStatus.*;
import static com.seatunnel.orchestrator.util.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

  private final OrchestrationProperties properties;
  private final ObjectMapper objectMapper;
  private final CommonUtil commonUtil;
  private final JobRepository jobRepository;
  private final PipelineService pipelineService;
  private final WebClient webClient;
  private final static Set<String> CDC_SOURCE_PLUGINS = Set.of(
    SourcePlugin.KAFKA.getValue(),
    SourcePlugin.MYSQL_CDC.getValue(),
    SourcePlugin.POSTGRESQL_CDC.getValue(),
    SourcePlugin.TIDB_CDC.getValue(),
    SourcePlugin.MONGODB_CDC.getValue(),
    SourcePlugin.ORACLE_CDC.getValue(),
    SourcePlugin.SQLSERVER_CDC.getValue()
  );
  private final PipelineValidator pipelineValidator;

  @ActionLog(operation = "Execute Pipeline")
  public Job executePipeline(String id, String jobId, Map<String, Object> env) {
    Pipeline pipeline = pipelineService.getById(id);

    pipelineValidator.validateEtlPipelineRequest(pipeline);

    if (StringUtils.isNotBlank(jobId)) {
      Job jobInfo = getJobsById(jobId);
      if (ObjectUtils.isEmpty(jobInfo)) {
        throw new ApiException(HttpStatus.NOT_FOUND,
          "Job with jobId:%s not found".formatted(jobId));
      }
      if (!jobInfo.isStoppedWithSavePoint()) {
        throw new ApiException(HttpStatus.BAD_REQUEST,
          ("Job with jobId:%s is not stopped with savepoint, " +
            "can not start").formatted(jobId));
      }
    }

    String jobMode = (String) env.get(JOB_MODE);
    if (StringUtils.isBlank(jobMode)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "job.mode can not be blank.");
    }
    try {
      JobMode.valueOf(jobMode.toUpperCase());
    } catch (IllegalArgumentException ex) {
      throw new ApiException(HttpStatus.BAD_REQUEST,
        "Invalid job.mode value", Map.of("Allowed values", JobMode.values()));
    }

    JobInstance instance = getJobInstance(pipeline);
    instance.setEnv(env);
    instance.getSource().stream()
      .map(source -> (String) source.get("plugin_name"))
      .filter(StringUtils::isNotBlank)
      .filter(pluginName -> !pluginName.equalsIgnoreCase(SourcePlugin.FAKESOURCE.getValue()))
      .forEach(pluginName -> {
        if (jobMode.equalsIgnoreCase(JobMode.STREAMING.getValue()) &&
          !CDC_SOURCE_PLUGINS.contains(pluginName)) {
          throw new ApiException(HttpStatus.BAD_REQUEST,
            "For streaming job, source plugin must be one of %s".formatted(CDC_SOURCE_PLUGINS));
        }
        if (jobMode.equalsIgnoreCase(JobMode.BATCH.getValue()) &&
          CDC_SOURCE_PLUGINS.contains(pluginName)) {
          throw new ApiException(HttpStatus.BAD_REQUEST,
            "For batch job, source plugin can not be one of %s".formatted(CDC_SOURCE_PLUGINS));
        }
      });

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
      .fromHttpUrl(properties.getEtlServiceUrl())
      .path("/submit-job")
      .queryParam("jobName", pipeline.getName());

    if (StringUtils.isNotEmpty(jobId)) {
      uriComponentsBuilder
        .queryParam(IS_START_WITH_SAVE_POINT, true)
        .queryParam(JOB_ID, jobId);
    }

    JsonNode jsonNode = commonUtil.restClient(
      instance,
      HttpMethod.POST,
      uriComponentsBuilder.toUriString());

    Job.JobBuilder jobBuilder = Job.builder()
      .createTime(Date.from(java.time.Instant.now()))
      .jobName(pipeline.getName())
      .pipelineId(pipeline.getId())
      .jobInstance(instance);

    if (ObjectUtils.isNotEmpty(jsonNode.get(JOB_ID))) {
      jobBuilder.jobStatus(JobStatus.RUNNING)
        .jobId(jsonNode.get(JOB_ID).asText())
        .id(jsonNode.get(JOB_ID).asText());
    } else {
      jobBuilder.jobStatus(JobStatus.SUBMITTED);
    }
    Job job = jobBuilder.build();

    jobRepository.save(job);

    return job;
  }

  @ActionLog(operation = "Get Job Details")
  private JobInstance getJobInstance(Pipeline pipeline) {
    JobInstance jobInstance = new JobInstance();
    // updating nodes config
    pipeline.getNodes().forEach(node -> {
      processNode(node, jobInstance);
    });

    return jobInstance;
  }

  @ActionLog(operation = "Process Node")
  private void processNode(Node node, JobInstance jobInstance) {

    switch (node.getPluginType()) {
      case PluginType.SOURCE:
        jobInstance.getSource().add(node.getConfig());
        break;
      case PluginType.TRANSFORM:
        jobInstance.getTransform().add(node.getConfig());
        break;
      case PluginType.SINK:
        jobInstance.getSink().add(node.getConfig());
        break;
    }
  }

  @ActionLog(operation = "Get Job Details By Id")
  public Job getJobsById(String jobId) {
    log.info("Fetching ETL job info for jobId: {}", jobId);

    Job job = validateJobExists(jobId);
    Job jobResponse = jobDetailsApiCall(jobId).block();
    if (ObjectUtils.isNotEmpty(jobResponse)) {
      jobResponse.setId(jobId);
      jobResponse.setPipelineId(job.getPipelineId());
      jobResponse.setJobName(job.getJobName());
      jobResponse.setJobInstance(job.getJobInstance());
      jobResponse.setCompletionTimeSec(calculateCompletionTime(jobResponse));
      jobRepository.save(jobResponse);
    }
    return jobResponse;
  }

  @ActionLog(operation = "Stop Job")
  private Job validateJobExists(String jobId) {
    Job job = jobRepository.findEtlJobByJobId(jobId);
    if (ObjectUtils.isEmpty(job)) {
      throw new ApiException(HttpStatus.NOT_FOUND,
        "Job with id:%s not found".formatted(jobId));
    }
    return job;
  }

  @ActionLog(operation = "Stop Job")
  public Map<String, String> stopJob(String jobId, boolean isStopWithSavePoint) {

    Job job = validateJobExists(jobId);

    JobStatus status = jobDetailsApiCall(jobId).map(Job::getJobStatus)
      .block();
    if (ObjectUtils.isNotEmpty(status)) {
      job.setJobStatus(status);
    }

    if (!job.getJobStatus().equals(JobStatus.RUNNING)) {
      throw new ApiException(HttpStatus.BAD_REQUEST,
        "Job with id:%s is not running, can not stop".formatted(jobId));
    }

    Map<String, Object> requestBody = Map.of(
      JOB_ID, Long.parseLong(jobId),
      IS_STOP_WITH_SAVE_POINT, isStopWithSavePoint
    );

    commonUtil.restClient(
      requestBody,
      HttpMethod.POST,
      properties.getEtlServiceUrl() + "/stop-job");

    job.setJobStatus(JobStatus.CANCELED);
    job.setStoppedWithSavePoint(isStopWithSavePoint);
    jobRepository.save(job);

    return Map.of("msg", String.format("Job cancelled with jobId : %s", jobId));
  }

  public void callback(JsonNode event) {

    String jobId = getJobId(event);
    if (StringUtils.isEmpty(jobId)) {
      log.warn("jobId not found in the first element.");
      return;
    }

    Job job = jobRepository.findEtlJobByJobId(jobId);
    if (ObjectUtils.isEmpty(job)) {
      log.warn("EtlJob not found in the db.");
      return;
    }

    JsonNode response = commonUtil.restClient(
      null,
      HttpMethod.GET,
      properties.getEtlServiceUrl() + "/job-info/" + jobId);

    log.info("Etl Job info response: {}", response);

    Job jobDetails;
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jobDetails = objectMapper.convertValue(response, Job.class);

    jobDetails.setCompletionTimeSec(calculateCompletionTime(jobDetails));
    jobDetails.setId(job.getId());

    jobRepository.save(jobDetails);

  }

  @ActionLog(operation = "Calculate Job Completion Time")
  private static long calculateCompletionTime(Job jobDetails) {
    long completionTime = -1L;

    if (ObjectUtils.isNotEmpty(jobDetails.getFinishTime())) {
      long diffInMillis =
        jobDetails.getFinishTime().getTime() - jobDetails.getCreateTime().getTime();
      // Convert milliseconds to seconds
      completionTime = diffInMillis / 1000;
    }

    return completionTime;
  }

  @ActionLog(operation = "Extract Job Id From Callback Event")
  private static String getJobId(JsonNode event) {
    // Access the first element in the array
    JsonNode firstElement = event.get(0);
    // Extract the jobId from the first element
    if (firstElement != null && firstElement.has(JOB_ID)) {
      return firstElement.get(JOB_ID).asText();
    } else {
      return null;
    }

  }

  public Flux<String> streamJobStatus(String jobId) {
    validateJobExists(jobId);
    return Flux.interval(Duration.ofSeconds(1))
      .flatMap(tick -> jobDetailsApiCall(jobId)
        .map(Job::getJobStatus)
        .map(JobStatus::name))
      .distinctUntilChanged() // Only send if status actually changes
      .takeUntil(status -> List.of("FINISHED", "CANCELED", "FAILED").contains(status));
  }

  public Mono<Job> jobDetailsApiCall(String jobId) {
    return webClient.get()
      .uri(properties.getEtlServiceUrl() + "/job-info/{id}", jobId)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(Job.class);
  }

  public JobOverview getJobsOverview() {
    log.info("Fetching ETL overview information");
    JsonNode response = commonUtil.restClient(
      null,
      HttpMethod.GET,
      properties.getEtlServiceUrl() + "/overview");

    return objectMapper.convertValue(
      response,
      JobOverview.class
    );
  }

  public List<JobProjection> getJobsByStatus(JobStatus status) {
    log.info("Fetching ETL jobs with status: {}", status);

    String url = properties.getEtlServiceUrl();
    if (status.equals(JobStatus.RUNNING)) {
      url += "/running-jobs";
    } else {
      url += "/finished-jobs/" + status;
    }
    log.info("Constructed URL for fetching ETL jobs: {}", url);
    JsonNode jsonNode = commonUtil.restClient(
      null,
      HttpMethod.GET,
      url);

    return objectMapper.convertValue(
      jsonNode,
      objectMapper.getTypeFactory().constructCollectionType(List.class, JobProjection.class)
    );

  }

}
