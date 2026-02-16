package com.seatunnel.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class BaseEntity implements Serializable {

  @Id
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String id;

  @NotBlank(message = "name can not be blank.")
  private String name;

  @CreatedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date createdOn;

  @LastModifiedDate
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private Date updatedOn;

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
  }

}
