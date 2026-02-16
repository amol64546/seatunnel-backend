package com.seatunnel.orchestrator.projection;

import java.util.Date;

public interface EtlPipelineProjection {

  String getId();

  String getName();

  Date getCreatedOn();
}
