
/*
 * Copyright (c) 2024. Gaian Solutions Pvt. Ltd. All rights reserved.
 */

package com.seatunnel.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document("etl_pipeline_instance")
public class EtlPipelineInstance implements Serializable {

  @Id
  @JsonIgnore
  private String id;

  private String pipelineId;

  @JsonProperty("env")
  private Map<String, Object> env;

  @JsonProperty("source")
  @Builder.Default
  private List<Map<String, Object>> sources = new ArrayList<>();

  @JsonProperty("transform")
  @Builder.Default
  private List<Map<String, Object>> transforms = new ArrayList<>();

  @JsonProperty("sink")
  @Builder.Default
  private List<Map<String, Object>> sinks = new ArrayList<>();

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

}



