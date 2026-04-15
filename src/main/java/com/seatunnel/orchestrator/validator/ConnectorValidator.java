package com.seatunnel.orchestrator.validator;

import com.seatunnel.orchestrator.enums.SinkPlugin;
import com.seatunnel.orchestrator.enums.SourcePlugin;
import com.seatunnel.orchestrator.enums.TransformPlugin;
import com.seatunnel.orchestrator.exception.ApiException;
import com.seatunnel.orchestrator.model.Connector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ConnectorValidator {

  @Before("@annotation(com.seatunnel.orchestrator.annotations.ConnectorValidation) && args(connector,..)")
  public void validateEtlBrick(Connector connector) {

    Map<String, Object> config = connector.getConfig();

    String pluginName = (String) config.get("plugin_name");

    if (StringUtils.isBlank(pluginName)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
        "config.plugin_name cannot be blank");
    }

    if (!connector.getPluginType().isValid(pluginName)) {
      throw new ApiException(HttpStatus.BAD_REQUEST,
        "config.plugin_name is invalid for plugin type: %s".formatted(connector.getPluginType()),
        Map.of("Allowed values", connector.getPluginType().getAllowedPluginNames()));
    }

    switch (connector.getPluginType()) {
      case SOURCE -> config.put("plugin_name",
        SourcePlugin.valueOf(pluginName.toUpperCase()).getValue());
      case TRANSFORM -> config.put("plugin_name",
        TransformPlugin.valueOf(pluginName.toUpperCase()).getValue());
      case SINK -> config.put("plugin_name",
        SinkPlugin.valueOf(pluginName.toUpperCase()).getValue());
    }

  }
}
