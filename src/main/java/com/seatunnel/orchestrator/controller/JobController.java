package com.seatunnel.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.seatunnel.orchestrator.enums.JobStatus;
import com.seatunnel.orchestrator.model.JobOverview;
import com.seatunnel.orchestrator.model.Job;
import com.seatunnel.orchestrator.service.JobService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.Map;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
@Slf4j
public class JobController {

  private final JobService jobService;

  @PostMapping(value = "/stop/{jobId}", produces = {"application/json"})
  public ResponseEntity<Map<String, String>> stopJob(@PathVariable String jobId,
                                                     @RequestParam(required = false) boolean isStopWithSavePoint) {
    log.info("POST:/v1.0/pipeline/etl/stop-job?jobId={}&isStopWithSavePoint={}", jobId,
      isStopWithSavePoint);
    return ResponseEntity.ok(jobService.stopJob(jobId, isStopWithSavePoint));
  }

  @GetMapping(value = "/{jobId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Job> getJobsById(
    @PathVariable String jobId) {
    return ResponseEntity.ok().body(jobService.getJobsById(jobId));
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> getJobsByStatus(
    @RequestParam JobStatus status) {
    return ResponseEntity.ok().body(jobService.getJobsByStatus(status));
  }

  @GetMapping(value = "/overview", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JobOverview> getJobsOverview() {
    return ResponseEntity.ok().body(jobService.getJobsOverview());
  }

  @GetMapping(value = "/status/{jobId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> streamJobStatus(@PathVariable String jobId) {
    return jobService.streamJobStatus(jobId);
  }

  @Hidden
  @PostMapping(value = "/callback", consumes = "application/json")
  public void jobStatusCallback(@RequestBody JsonNode event) {
    jobService.callback(event);
  }
}
