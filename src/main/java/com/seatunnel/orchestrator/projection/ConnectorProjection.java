package com.seatunnel.orchestrator.projection;

import com.seatunnel.orchestrator.enums.PluginType;

public interface ConnectorProjection {

    String getId();
    PluginType getPluginType();
    String getName();
}
