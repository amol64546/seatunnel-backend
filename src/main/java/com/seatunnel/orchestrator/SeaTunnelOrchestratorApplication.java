package com.seatunnel.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class SeaTunnelOrchestratorApplication {

  public static void main(String[] args) {
    SpringApplication.run(SeaTunnelOrchestratorApplication.class, args);
  }

}
