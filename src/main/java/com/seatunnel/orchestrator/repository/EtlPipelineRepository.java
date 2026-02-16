package com.seatunnel.orchestrator.repository;

import com.seatunnel.orchestrator.model.EtlPipeline;
import com.seatunnel.orchestrator.projection.EtlPipelineProjection;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EtlPipelineRepository extends MongoRepository<EtlPipeline, String> {

  EtlPipelineProjection findProjectedById(String id);
}
