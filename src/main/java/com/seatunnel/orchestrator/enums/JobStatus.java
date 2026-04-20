

package com.seatunnel.orchestrator.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobStatus {

  RUNNING("RUNNING"),
  FINISHED("FINISHED"),
  CANCELED("CANCELED"),
  FAILED("FAILED"),

  SUBMITTED("SUBMITTED");

  private final String value;

  JobStatus(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

}
