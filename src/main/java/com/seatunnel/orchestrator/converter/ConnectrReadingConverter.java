package com.seatunnel.orchestrator.converter;

import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.model.Connector;
import com.seatunnel.orchestrator.util.MapKeyConverter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

@ReadingConverter
public class ConnectrReadingConverter implements Converter<Document, Connector> {

  @Override
  public Connector convert(Document source) {

    Connector brick = new Connector();

    brick.setId(source.get("_id").toString());
    brick.setName(source.getString("name"));
    brick.setCreatedOn(source.getDate("createdOn"));
    brick.setUpdatedOn(source.getDate("updatedOn"));
    brick.setPluginType(PluginType.valueOf(source.getString("pluginType").toUpperCase()));

    // Convert config: hash -> dot
    Object config = source.get("config");
    if (config instanceof Map) {
      brick.setConfig(MapKeyConverter.convertHashToDot((Map<String, Object>) config));
    }

    return brick;
  }
}