package com.seatunnel.orchestrator.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

  private final OrchestrationProperties orchestrationProperties;

  @Bean
  public RestTemplate restTemplate() {
    log.info("--------RestTemplate bean created successfully for master profile-------");
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.getInterceptors().add((request, body, execution) -> {
      request.getHeaders().setBasicAuth(
        orchestrationProperties.getEtlServiceUsername(),
        orchestrationProperties.getEtlServicePassword()
      );
      return execution.execute(request, body);
    });
    return restTemplate;
  }
}
