package com.seatunnel.orchestrator.projection;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seatunnel.orchestrator.enums.JobStatus;
import java.util.Date;

public record JobProjection(
  String jobId,
  String jobName,
  JobStatus jobStatus,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  Date createTime,
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
  Date finishTime,
    boolean stoppedWithSavePoint
) {}