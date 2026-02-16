
/*
 * Gaian Copyright
 * Copyright (C) : Gaian Solutions Ltd
 */
package com.seatunnel.orchestrator.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobStatus {

  SUBMITTED("SUBMITTED"),
  RUNNING("RUNNING"),
  FINISHED("FINISHED"),
  CANCELED("CANCELED"),
  FAILED("FAILED"),
  UNKNOWABLE("UNKNOWABLE");

  private final String value;

  JobStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

}
