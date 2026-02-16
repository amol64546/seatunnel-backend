package com.seatunnel.orchestrator.projection;

import com.seatunnel.orchestrator.enums.PluginType;

public interface EtlBrickProjection {

    String getId();
    PluginType getPluginType();
    String getName();
}
