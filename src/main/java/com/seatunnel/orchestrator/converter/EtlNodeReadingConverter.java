package com.seatunnel.orchestrator.converter;

import com.seatunnel.orchestrator.enums.PluginType;
import com.seatunnel.orchestrator.model.Node;
import com.seatunnel.orchestrator.util.MapKeyConverter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Map;

@ReadingConverter
public class EtlNodeReadingConverter implements Converter<Document, Node> {

  @Override
  public Node convert(Document source) {

    Node node = new Node();

    node.setId(source.getString("id"));
    node.setName(source.getString("name"));
    node.setConnectorId(source.getString("connectorId"));
    node.setPluginType(PluginType.valueOf(source.getString("pluginType").toUpperCase()));

    // Convert config: hash -> dot
    Object config = source.get("config");
    if (config instanceof Map) {
      node.setConfig(MapKeyConverter.convertHashToDot((Map<String, Object>) config));
    }

    return node;
  }
}