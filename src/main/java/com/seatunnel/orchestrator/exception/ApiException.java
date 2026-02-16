package com.seatunnel.orchestrator.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {
  private final HttpStatus httpStatus;
  private Object errors;
  private String errorMessage;

  public ApiException(HttpStatus httpStatus, String errorMessage) {
    this.httpStatus = httpStatus;
    this.errorMessage = errorMessage;
  }

  public ApiException(HttpStatus httpStatus, Object errors) {
    this.httpStatus = httpStatus;
    this.errors = errors;
  }

  public ApiException(HttpStatus httpStatus, String errorMessage, Object errors) {
    this.httpStatus = httpStatus;
    this.errors = errors;
    this.errorMessage = errorMessage;
  }
}
