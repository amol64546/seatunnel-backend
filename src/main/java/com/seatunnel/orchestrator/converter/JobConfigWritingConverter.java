package com.seatunnel.orchestrator.converter;

import com.seatunnel.orchestrator.util.MapKeyConverter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

@WritingConverter
public class JobConfigWritingConverter implements Converter<Map<String, Object>, Document> {

  @Override
  public Document convert(Map<String, Object> source) {
    return new Document(MapKeyConverter.convertDotToHash(source));
  }
}
