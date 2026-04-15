
/*
 * Copyright (c) 2024.
 * Gaian Solutions Pvt. Ltd.
 * All rights reserved.
 */

package com.seatunnel.orchestrator.repository;

import com.seatunnel.orchestrator.model.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobRepo extends MongoRepository<Job, String> {


  Job findEtlJobByJobId(String jobId);

}
