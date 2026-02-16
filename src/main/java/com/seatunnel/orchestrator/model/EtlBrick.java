package com.seatunnel.orchestrator.model;

import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.model.BaseEntity;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Document("etl_brick")
public class EtlBrick extends BaseEntity {

  @NotNull(message = "pluginType can not be null")
  private PluginType pluginType;

  @NotEmpty(message = "config can not be empty")
  private Map<String, Object> config = new HashMap<>();

}
