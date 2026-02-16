package com.seatunnel.orchestrator.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
    log.error("ResponseStatusException: ", ex);
    Map<String, Object> body = new HashMap<>();
    body.put("status", ex.getStatusCode().value());
    body.put("error", ex.getReason());

    return ResponseEntity
      .status(ex.getStatusCode())
      .body(body);
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
    log.error("ApiException: ", ex);
    Map<String, Object> body = new HashMap<>();
    body.put("status", ex.getHttpStatus().value());

    if (ex.getErrorMessage() != null) {
      body.put("errorMessage", ex.getErrorMessage());
    }
    if (ex.getErrors() != null) {
      body.put("errorObject", ex.getErrors());
    }
    return ResponseEntity
      .status(ex.getHttpStatus())
      .body(body);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    log.error("HttpMessageNotReadableException: ", ex);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleException(Exception ex) {
    log.error("Exception: ", ex);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error("IllegalArgumentException: ", ex);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("status", HttpStatus.BAD_REQUEST.value());
    body.put("message", ex.getMessage());

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(body);
  }
}
