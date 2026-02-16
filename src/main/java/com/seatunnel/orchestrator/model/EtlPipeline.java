package com.seatunnel.orchestrator.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.seatunnel.orchestrator.model.BaseEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Document("etl_pipeline")
public class EtlPipeline extends BaseEntity {

  @NotEmpty(message = "nodes can not be empty")
  @Valid
  private List<Node> nodes;

  @NotEmpty(message = "edges can not be empty")
  @Valid
  @JsonDeserialize(as = LinkedHashSet.class)
  private Set<Edge> edges;


  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
