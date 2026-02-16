package com.seatunnel.orchestrator.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum TransformPlugin {

  COPY("Copy"),
  FILTER("Filter"),
  SQL("Sql"),
  FIELDMAPPER("FieldMapper"),
  REPLACE("Replace"),
  SPLIT("Split"),
  FIELDRENAME("FieldRename"),
  LLM("LLM"),
  METADATA("Metadata"),
  EMBEDDING("Embedding"),
  DYNAMICCOMPILE("DynamicCompile"),
  JSONPATH("JsonPath"),
  ROWKINDEXTRACTOR("RowKindExtractor"),
  FILTERROWKIND("FilterRowKind"),
  TABLEMERGE("TableMerge"),
  TABLERENAME("TableRename"),
  TABLEFILTER("TableFilter");


  private final String value;

  TransformPlugin(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
