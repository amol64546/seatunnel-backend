
/*
 * Copyright (c) 2024. Gaian Solutions Pvt. Ltd. All rights reserved.
 */

package com.seatunnel.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.seatunnel.orchestrator.enums.JobStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.bson.json.JsonObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Document("etl_job_status")
public class ETLJobStatus implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @JsonIgnore
  private String id;

  private String jobId;

  // Extra field
  private String pipelineId;

  private String jobName;

  private boolean isStoppedWithSavePoint;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Date createTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  private Date finishTime;

  // Extra field
  private Long completionTimeSec;

  private Map<String, Object> envOptions;

  private JobStatus jobStatus;

  private String errorMsg;

  private Map<String, Object> metrics;

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

}
