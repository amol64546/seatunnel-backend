

package com.seatunnel.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.seatunnel.orchestrator.enums.PluginType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Node implements Serializable {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String name;

  @NotBlank(message = "connectorId can not be blank")
  private String connectorId;

  @NotBlank(message = "id can not be blank")
  private String id;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private PluginType pluginType;

  @Builder.Default
  private Map<String, Object> config = new HashMap<>();

  @Builder.Default
  private Map<String, Object> metadata = new HashMap<>();

  @JsonSetter("config")
  public void bindConfig(Map<String, Object> config) {
    if (config != null) {
      config.remove("plugin_name");
    }
    this.config = config;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

}
