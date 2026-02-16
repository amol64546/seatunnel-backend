package com.seatunnel.orchestrator.repository;

import com.seatunnel.orchestrator.model.EtlBrick;
import com.seatunnel.orchestrator.projection.EtlBrickProjection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EtlBrickRepository extends MongoRepository<EtlBrick, String> {

  List<EtlBrickProjection> findAllProjectedByIdIn(List<String> ids);

  EtlBrickProjection findProjectedById(String id);

  EtlBrick findEtlBrickById(String id);

}