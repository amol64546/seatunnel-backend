package com.seatunnel.orchestrator.controller;

import com.seatunnel.orchestrator.model.ETLJobStatus;
import com.seatunnel.orchestrator.model.EtlPipeline;
import com.seatunnel.orchestrator.service.EtlJobService;
import com.seatunnel.orchestrator.service.EtlPipelineService;
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

}
