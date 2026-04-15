package com.seatunnel.orchestrator.controller;

import com.seatunnel.orchestrator.model.Connector;
import com.seatunnel.orchestrator.service.ConnectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/connectors")
@RequiredArgsConstructor
@Slf4j
public class ConnectorController {

  private final ConnectorService service;

  @PostMapping
  public ResponseEntity<?> create(
    @RequestBody Connector request) {
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

  @PutMapping("/{id}")
  public ResponseEntity<?> put(
    @PathVariable String id,
    @RequestBody Connector request) {
    return ResponseEntity.ok(service.update(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(
    @PathVariable String id) {
    return ResponseEntity.ok(Map.of("msg", service.delete(id)));
  }

}
