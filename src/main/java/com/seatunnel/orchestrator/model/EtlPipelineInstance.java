
/*
 * Copyright (c) 2024. Gaian Solutions Pvt. Ltd. All rights reserved.
 */

package com.seatunnel.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EtlPipelineInstance implements Serializable {

  private Map<String, Object> env;

  @Builder.Default
  private List<Map<String, Object>> source = new ArrayList<>();

  @Builder.Default
  private List<Map<String, Object>> transform = new ArrayList<>();

  @Builder.Default
  private List<Map<String, Object>> sink = new ArrayList<>();

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

}



