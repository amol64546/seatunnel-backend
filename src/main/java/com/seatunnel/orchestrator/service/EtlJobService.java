package com.seatunnel.orchestrator.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.seatunnel.orchestrator.config.OrchestrationProperties;
import com.seatunnel.orchestrator.enums.JobMode;
import com.seatunnel.orchestrator.enums.JobStatus;
import com.seatunnel.orchestrator.exception.ApiException;
import com.seatunnel.orchestrator.model.ETLJobOverview;
import com.seatunnel.orchestrator.model.ETLJobStatus;
import com.seatunnel.orchestrator.model.EtlPipelineInstance;
import com.seatunnel.orchestrator.projection.EtlPipelineProjection;
import com.seatunnel.orchestrator.repository.EtlJobStatusRepo;
import com.seatunnel.orchestrator.repository.EtlPipelineInstanceRepo;
import com.seatunnel.orchestrator.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Date;
import java.util.Map;

import static com.seatunnel.orchestrator.util.Constants.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EtlJobService {

  private final EtlPipelineInstanceRepo etlPipelineInstanceRepo;
  private final OrchestrationProperties properties;
  private final ObjectMapper objectMapper;
  private final CommonUtil commonUtil;
  private final EtlJobStatusRepo etlJobStatusRepo;
  private final EtlPipelineService etlPipelineService;


  public ETLJobStatus executePipeline(String id, String jobId, Map<String, Object> env) {
    EtlPipelineProjection etlPipeline = etlPipelineService.getEtlPipelineProjection(id);

    if (StringUtils.isNotBlank(jobId)) {
      ETLJobStatus jobInfo = getJobsById(jobId);
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

    EtlPipelineInstance instance = etlPipelineInstanceRepo.getEtlPipelineInstanceByPipelineId(id)
      .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
        "Etl pipeline instance not found for id: %s".formatted(id)));
    instance.setEnv(env);

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
      .fromHttpUrl(properties.getEtlServiceUrl())
      .path("/submit-job")
      .queryParam("jobName", etlPipeline.getName());

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

    ETLJobStatus etlJobStatus = ETLJobStatus.builder()
      .jobId(jsonNode.get(JOB_ID).asText())
      .jobStatus(JobStatus.SUBMITTED)
      .jobName(etlPipeline.getName())
      .pipelineId(etlPipeline.getId())
      .createTime(Date.from(java.time.Instant.now()))
      .envOptions(env)
      .build();

    etlJobStatusRepo.save(etlJobStatus);

    return etlJobStatus;
  }

  public ETLJobStatus getJobsById(String jobId) {
    log.info("Fetching ETL job info for jobId: {}", jobId);

    ETLJobStatus etlJobStatus = validateJobExists(jobId);
    if (!etlJobStatus.getJobStatus().equals(JobStatus.SUBMITTED)) {
      return etlJobStatus;
    }

    JsonNode response = commonUtil.restClient(
      null,
      HttpMethod.GET,
      properties.getEtlServiceUrl() + "/job-info/" + jobId,
      null);

    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    ETLJobStatus etlJobStatusResponse = objectMapper.convertValue(response, ETLJobStatus.class);
    etlJobStatusResponse.setPipelineId(etlJobStatus.getPipelineId());
    etlJobStatusResponse.setCompletionTimeSec(etlJobStatus.getCompletionTimeSec());
    etlJobStatusResponse.setId(etlJobStatus.getId());
    return etlJobStatusRepo.save(etlJobStatusResponse);
  }

  private ETLJobStatus validateJobExists(String jobId) {
    ETLJobStatus etlJobStatus = etlJobStatusRepo.findEtlJobByJobId(jobId);
    if (ObjectUtils.isEmpty(etlJobStatus)) {
      throw new ApiException(HttpStatus.NOT_FOUND,
        "Job with id:%s not found".formatted(jobId));
    }
    return etlJobStatus;
  }

  public Map<String, String> stopJob(String jobId, boolean isStopWithSavePoint) {

    ETLJobStatus etlJobStatus = validateJobExists(jobId);

    if (!etlJobStatus.getJobStatus().equals(JobStatus.RUNNING)) {
      throw new ApiException(HttpStatus.BAD_REQUEST,
        "Job with id:%s is not running, can not stop with savepoint".formatted(jobId));
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

    etlJobStatus.setJobStatus(JobStatus.CANCELED);
    etlJobStatus.setStoppedWithSavePoint(isStopWithSavePoint);
    etlJobStatusRepo.save(etlJobStatus);

    return Map.of("msg", String.format("Job cancelled with jobId : %s", jobId));
  }

  public ETLJobOverview getJobsOverview() {
    log.info("Fetching ETL overview information");
    JsonNode response = commonUtil.restClient(
      null,
      HttpMethod.GET,
      properties.getEtlServiceUrl() + "/overview",
      null);

    return objectMapper.convertValue(
      response,
      ETLJobOverview.class
    );
  }

  public Object getJobsByStatus(JobStatus status) {
    log.info("Fetching ETL jobs with status: {}", status);

    String url = properties.getEtlServiceUrl();
    if (ObjectUtils.isEmpty(status)) {
      url += "/finished-jobs";
    } else if (status.equals(JobStatus.RUNNING)) {
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


  public void callback(JsonNode event) {

    String jobId = getJobId(event);
    if (StringUtils.isEmpty(jobId)) {
      log.warn("jobId not found in the first element.");
      return;
    }

    ETLJobStatus etlJobStatus = etlJobStatusRepo.findEtlJobByJobId(jobId);
    if (ObjectUtils.isEmpty(etlJobStatus)) {
      log.warn("EtlJob not found in the db.");
      return;
    }

    JsonNode response = commonUtil.restClient(
      null,
      HttpMethod.GET,
      properties.getEtlServiceUrl() + "/job-info/" + jobId,
      null);

    log.info("Etl Job info response: {}", response);

    ETLJobStatus jobDetails;
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    jobDetails = objectMapper.convertValue(response, ETLJobStatus.class);

    long diffInSeconds = -1L;

    if (ObjectUtils.isNotEmpty(jobDetails.getFinishTime())) {
      long diffInMillis =
        jobDetails.getFinishTime().getTime() - jobDetails.getCreateTime().getTime();
      // Convert milliseconds to seconds
      diffInSeconds = diffInMillis / 1000;
    }

    jobDetails.setCompletionTimeSec(diffInSeconds);
    jobDetails.setId(etlJobStatus.getId());

    etlJobStatusRepo.save(jobDetails);

  }

  public static String getJobId(JsonNode event) {
    // Access the first element in the array
    JsonNode firstElement = event.get(0);
    // Extract the jobId from the first element
    if (firstElement != null && firstElement.has(JOB_ID)) {
      return firstElement.get(JOB_ID).asText();
    } else {
      return null;
    }

  }
}
