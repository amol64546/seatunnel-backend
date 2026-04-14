package com.seatunnel.orchestrator.converter;

import com.seatunnel.orchestrator.model.Node;
import com.seatunnel.orchestrator.util.MapKeyConverter;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class EtlNodeWritingConverter implements Converter<Node, Document> {

  @Override
  public Document convert(Node source) {
    Document doc = new Document();

    doc.put("id", source.getId());
    doc.put("pluginType", source.getPluginType());
    doc.put("name", source.getName());
    doc.put("connectorId", source.getConnectorId());

    if (source.getConfig() != null) {
      doc.put("config", MapKeyConverter.convertDotToHash(source.getConfig()));
    }

    return doc;
  }
}