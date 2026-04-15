package com.seatunnel.orchestrator.converter;

import com.seatunnel.orchestrator.util.MapKeyConverter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.HashMap;
import java.util.Map;

@ReadingConverter
public class JobConfigReadingConverter implements Converter<Document, Map<String, Object>> {

  @Override
  public Map<String, Object> convert(Document source) {
    return MapKeyConverter.convertHashToDot(new HashMap<>(source));
  }

}
