package com.seatunnel.orchestrator.repository;

import com.seatunnel.orchestrator.model.Pipeline;
import com.seatunnel.orchestrator.projection.PipelineProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PipelineRepository extends MongoRepository<Pipeline, String> {

  PipelineProjection findProjectedById(String id);

  Page<PipelineProjection> findAllProjectedBy(Pageable pageable);
}
