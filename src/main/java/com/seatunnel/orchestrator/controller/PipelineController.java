package com.seatunnel.orchestrator.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.seatunnel.orchestrator.enums.JobStatus;
import com.seatunnel.orchestrator.model.ETLJobOverview;
import com.seatunnel.orchestrator.model.ETLJobStatus;
import com.seatunnel.orchestrator.model.EtlPipeline;
import com.seatunnel.orchestrator.service.EtlJobService;
import com.seatunnel.orchestrator.service.EtlPipelineService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pipelines")
@RequiredArgsConstructor
@Slf4j
public class PipelineController {

  private final EtlPipelineService service;
  private final EtlJobService etlJobService;


  @PostMapping
  public ResponseEntity<?> create(
    @RequestBody EtlPipeline request) {
    return ResponseEntity.ok(service.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(
    @PathVariable String id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @GetMapping
  public ResponseEntity<?> getAll(
    @PageableDefault Pageable pageable) {
    return ResponseEntity.ok(service.getAll(pageable));
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> put(
    @PathVariable String id,
    @RequestBody EtlPipeline request) {
    return ResponseEntity.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(
    @PathVariable String id) {
    return ResponseEntity.ok(Map.of("msg", service.delete(id)));
  }

  @PostMapping(value = "/execute/{id}", produces = {"application/json"})
  public ResponseEntity<ETLJobStatus> executePipeline(
    @PathVariable String id,
    @RequestParam(required = false) String jobId,
    @RequestBody Map<String, Object> env) {
    log.info("POST: /v1.0/pipeline/etl/execute/{}", id);
    return ResponseEntity.ok(
      etlJobService.executePipeline(id, jobId, env));
  }

  @GetMapping(value = "/jobs/{jobId}", produces = {"application/json"})
  public ResponseEntity<ETLJobStatus> getJobsById(@PathVariable String jobId) {
    return ResponseEntity.ok().body(etlJobService.getJobsById(jobId));
  }

  @GetMapping(value = "/jobs", produces = {"application/json"})
  public ResponseEntity<Object> getJobsByStatus(@RequestParam JobStatus status) {
    return ResponseEntity.ok().body(etlJobService.getJobsByStatus(status));
  }

  @GetMapping(value = "/jobs/overview", produces = {"application/json"})
  public ResponseEntity<ETLJobOverview> getJobsOverview() {
    return ResponseEntity.ok().body(etlJobService.getJobsOverview());
  }

  @PostMapping(value = "/jobs/stop/{jobId}", produces = {"application/json"})
  public ResponseEntity<Map<String, String>> stopJob(@PathVariable String jobId,
                                                     @RequestParam(required = false) boolean isStopWithSavePoint) {
    log.info("POST:/v1.0/pipeline/etl/stop-job?jobId={}&isStopWithSavePoint={}", jobId,
      isStopWithSavePoint);
    return ResponseEntity.ok(etlJobService.stopJob(jobId, isStopWithSavePoint));
  }

  @Hidden
  @PostMapping(value = "/jobs/callback", consumes = "application/json")
  public void jobStatusCallback(@RequestBody JsonNode event) {
    etlJobService.callback(event);
  }

}
