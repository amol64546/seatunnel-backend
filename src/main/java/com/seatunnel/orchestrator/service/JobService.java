package com.seatunnel.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.seatunnel.orchestrator.config.OrchestrationProperties;
import com.seatunnel.orchestrator.enums.JobMode;
import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.enums.SourcePlugin;
import com.seatunnel.orchestrator.exception.ApiException;
import com.seatunnel.orchestrator.model.*;
import com.seatunnel.orchestrator.repository.JobRepo;
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

import static com.seatunnel.orchestrator.util.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class JobService {

  private final OrchestrationProperties properties;
  private final ObjectMapper objectMapper;
  private final CommonUtil commonUtil;
  private final JobRepo jobRepo;
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

    if (ObjectUtils.isNotEmpty(jobId)) {
      uriComponentsBuilder
        .queryParam(IS_START_WITH_SAVE_POINT, true)
        .queryParam(JOB_ID, jobId);
    }

    JsonNode jsonNode = commonUtil.restClient(
      instance,
      HttpMethod.POST,
      uriComponentsBuilder.toUriString(),
      null);

    Job job = Job.builder()
      .jobId(jsonNode.get(JOB_ID).asText())
      .jobStatus(com.seatunnel.orchestrator.enums.JobStatus.SUBMITTED)
      .jobName(pipeline.getName())
      .pipelineId(pipeline.getId())
      .createTime(Date.from(java.time.Instant.now()))
      .envOptions(env)
      .jobInstance(instance)
      .build();

    jobRepo.save(job);

    return job;
  }

  private JobInstance getJobInstance(Pipeline pipeline) {
    JobInstance jobInstance = new JobInstance();
    // updating nodes config
    pipeline.getNodes().forEach(node -> {
      processNode(node, jobInstance);
    });

    return jobInstance;
  }

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


  public Job getJobsById(String jobId) {
    log.info("Fetching ETL job info for jobId: {}", jobId);

    Job job = validateJobExists(jobId);
    if (!job.getJobStatus().equals(com.seatunnel.orchestrator.enums.JobStatus.SUBMITTED)) {
      return job;
    }
    return job;
  }

  private Job validateJobExists(String jobId) {
    Job job = jobRepo.findEtlJobByJobId(jobId);
    if (ObjectUtils.isEmpty(job)) {
      throw new ApiException(HttpStatus.NOT_FOUND,
        "Job with id:%s not found".formatted(jobId));
    }
    return job;
  }

  public Map<String, String> stopJob(String jobId, boolean isStopWithSavePoint) {

    Job job = validateJobExists(jobId);

    if (!job.getJobStatus().equals(com.seatunnel.orchestrator.enums.JobStatus.RUNNING)) {
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
      properties.getEtlServiceUrl() + "/stop-job",
      null);

    job.setJobStatus(com.seatunnel.orchestrator.enums.JobStatus.CANCELED);
    job.setStoppedWithSavePoint(isStopWithSavePoint);
    jobRepo.save(job);

    return Map.of("msg", String.format("Job cancelled with jobId : %s", jobId));
  }


  public void callback(JsonNode event) {

    String jobId = getJobId(event);
    if (StringUtils.isEmpty(jobId)) {
      log.warn("jobId not found in the first element.");
      return;
    }

    Job job = jobRepo.findEtlJobByJobId(jobId);
    if (ObjectUtils.isEmpty(job)) {
      log.warn("EtlJob not found in the db.");
      return;
    }

    JsonNode response = commonUtil.restClient(
      null,
      HttpMethod.GET,
      properties.getEtlServiceUrl() + "/job-info/" + jobId,
      null);

    log.info("Etl Job info response: {}", response);

    Job jobDetails;
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jobDetails = objectMapper.convertValue(response, Job.class);

    long diffInSeconds = -1L;

    if (ObjectUtils.isNotEmpty(jobDetails.getFinishTime())) {
      long diffInMillis =
        jobDetails.getFinishTime().getTime() - jobDetails.getCreateTime().getTime();
      // Convert milliseconds to seconds
      diffInSeconds = diffInMillis / 1000;
    }

    jobDetails.setCompletionTimeSec(diffInSeconds);
    jobDetails.setId(job.getId());

    jobRepo.save(jobDetails);

  }

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
    return Flux.interval(Duration.ofSeconds(2))
      .flatMap(tick -> pollStatus(jobId))
      .distinctUntilChanged() // Only send if status actually changes
      .takeUntil(status -> List.of("FINISHED", "CANCELED", "FAILED").contains(status));
  }

  public Mono<String> pollStatus(String jobId) {
    return webClient.get()
      .uri(properties.getEtlServiceUrl() + "/job-info/{id}", jobId)
      .accept(MediaType.APPLICATION_JSON)
      .retrieve()
      .bodyToMono(JsonNode.class)
      .map(json -> json.path("jobStatus").asText())
      .onErrorResume(e -> Mono.just("ERROR")); // Handle downstream failures gracefully
  }

  public JobOverview getJobsOverview() {
    log.info("Fetching ETL overview information");
    JsonNode response = commonUtil.restClient(
      null,
      HttpMethod.GET,
      properties.getEtlServiceUrl() + "/overview",
      null);

    return objectMapper.convertValue(
      response,
      JobOverview.class
    );
  }

  public Object getJobsByStatus(com.seatunnel.orchestrator.enums.JobStatus status) {
    log.info("Fetching ETL jobs with status: {}", status);

    String url = properties.getEtlServiceUrl();
    if (ObjectUtils.isEmpty(status)) {
      url += "/finished-jobs";
    } else if (status.equals(com.seatunnel.orchestrator.enums.JobStatus.RUNNING)) {
      url += "/running-jobs";
    } else {
      url += "/finished-jobs/" + status;
    }
    log.info("Constructed URL for fetching ETL jobs: {}", url);
    return commonUtil.restClient(
      null,
      HttpMethod.GET,
      url,
      null);
  }

}
