
/*
 * Gaian Copyright
 * Copyright (C) : Gaian Solutions Ltd
 */
package com.seatunnel.orchestrator.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum JobMode {

  STREAMING("STREAMING"),
  BATCH("BATCH");

  private final String value;

  JobMode(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

}
