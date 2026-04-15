package com.seatunnel.orchestrator.projection;

import java.util.Date;

public interface PipelineProjection {

  String getId();

  String getName();

  Date getCreatedOn();
}
