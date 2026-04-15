package com.seatunnel.orchestrator.controller;

import com.seatunnel.orchestrator.model.Job;
import com.seatunnel.orchestrator.model.Pipeline;
import com.seatunnel.orchestrator.service.JobService;
import com.seatunnel.orchestrator.service.PipelineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pipelines")
@RequiredArgsConstructor
@Slf4j
public class PipelineController {

  private final PipelineService service;
  private final JobService jobService;


  @PostMapping
  public ResponseEntity<?> create(
    @RequestBody Pipeline request) {
    return ResponseEntity.ok(service.create(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getById(
    @PathVariable String id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @GetMapping
  public ResponseEntity<?> getAll(
    @PageableDefault(sort = "updatedOn", direction = Sort.Direction.DESC) Pageable pageable) {
    return ResponseEntity.ok(service.getAll(pageable));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(
    @PathVariable String id) {
    return ResponseEntity.ok(Map.of("msg", service.delete(id)));
  }

  @PostMapping(value = "/execute/{id}", produces = {"application/json"})
  public ResponseEntity<Job> executePipeline(
    @PathVariable String id,
    @RequestParam(required = false) String jobId,
    @RequestBody Map<String, Object> env) {
    log.info("POST: /v1.0/pipeline/etl/execute/{}", id);
    return ResponseEntity.ok(
      jobService.executePipeline(id, jobId, env));
  }

}
