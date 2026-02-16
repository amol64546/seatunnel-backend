
/*
 * Copyright (c) 2024.
 * Gaian Solutions Pvt. Ltd.
 * All rights reserved.
 */

package com.seatunnel.orchestrator.repository;

import com.seatunnel.orchestrator.model.ETLJobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EtlJobStatusRepo extends MongoRepository<ETLJobStatus, String> {


  ETLJobStatus findEtlJobByJobId(String jobId);

}
