package com.seatunnel.orchestrator.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Document("pipelines")
public class Pipeline extends BaseEntity {

  @Id
  private String id;

  @NotEmpty(message = "nodes can not be empty")
  @Valid
  private List<Node> nodes = new ArrayList<>();

  @NotEmpty(message = "edges can not be empty")
  @Valid
  @JsonDeserialize(as = LinkedHashSet.class)
  private Set<Edge> edges = new LinkedHashSet<>();


  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }
}
