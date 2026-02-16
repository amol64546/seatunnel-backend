package com.seatunnel.orchestrator.repository;

import com.seatunnel.orchestrator.model.EtlPipelineInstance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EtlPipelineInstanceRepo extends MongoRepository<EtlPipelineInstance, String> {

  Optional<EtlPipelineInstance> getEtlPipelineInstanceByPipelineId(String id);
}
