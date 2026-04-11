package com.seatunnel.orchestrator.converter;

import com.seatunnel.orchestrator.model.EtlBrick;
import com.seatunnel.orchestrator.util.MapKeyConverter;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class EtlBrickWritingConverter implements Converter<EtlBrick, Document> {

  @Override
  public Document convert(EtlBrick source) {
    Document doc = new Document();

    if (source.getId() != null) {
      doc.put("_id", new ObjectId(source.getId()));
    }

    doc.put("name", source.getName());
    doc.put("pluginType", source.getPluginType());
    doc.put("createdOn", source.getCreatedOn());
    doc.put("updatedOn", source.getUpdatedOn());

    if (source.getConfig() != null) {
      doc.put("config", MapKeyConverter.convertDotToHash(source.getConfig()));
    }

    return doc;
  }
}