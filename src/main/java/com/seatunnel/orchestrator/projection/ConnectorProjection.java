package com.seatunnel.orchestrator.projection;

import com.seatunnel.orchestrator.enums.PluginType;

import java.util.Date;

public interface ConnectorProjection {

  String getId();

  PluginType getPluginType();

  String getName();

  Date getCreatedOn();

  Date getUpdatedOn();
}
