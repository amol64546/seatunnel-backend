package com.seatunnel.orchestrator.converter;

import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.model.EtlBrick;
import com.seatunnel.orchestrator.util.MapKeyConverter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

@ReadingConverter
public class EtlBrickReadingConverter implements Converter<Document, EtlBrick> {

  @Override
  public EtlBrick convert(Document source) {

    EtlBrick brick = new EtlBrick();

    brick.setId(source.get("_id").toString());
    brick.setName(source.getString("name"));
    brick.setPluginType(PluginType.valueOf(source.getString("pluginType").toUpperCase()));

    // Convert config: hash -> dot
    Object config = source.get("config");
    if (config instanceof Map) {
      brick.setConfig(MapKeyConverter.convertHashToDot((Map<String, Object>) config));
    }

    return brick;
  }
}