
/*
 * Copyright (c) 2024. Gaian Solutions Pvt. Ltd. All rights reserved.
 */
package com.seatunnel.orchestrator.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "orchestration", ignoreInvalidFields = true)
public class OrchestrationProperties {
    private String etlServiceUrl;

}
