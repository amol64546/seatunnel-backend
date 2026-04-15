package com.seatunnel.orchestrator.repository;

import com.seatunnel.orchestrator.model.Connector;
import com.seatunnel.orchestrator.projection.ConnectorProjection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ConnectorRepository extends MongoRepository<Connector, String> {

  List<ConnectorProjection> findAllProjectedByIdIn(List<String> ids);

  ConnectorProjection findProjectedById(String id);

  Connector findEtlBrickById(String id);

}