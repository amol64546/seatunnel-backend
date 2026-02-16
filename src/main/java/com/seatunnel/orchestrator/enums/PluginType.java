package com.seatunnel.orchestrator.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum PluginType {

  SOURCE(SourcePlugin.values()),
  TRANSFORM(TransformPlugin.values()),
  SINK(SinkPlugin.values());

  private final Set<String> allowedPluginNames;

  PluginType(Enum<?>[] plugins) {
    this.allowedPluginNames = Arrays.stream(plugins)
      .map(Enum::name)
      .collect(Collectors.toSet());
  }

  public boolean isValid(String pluginName) {
    return allowedPluginNames.contains(pluginName.toUpperCase());
  }

}
