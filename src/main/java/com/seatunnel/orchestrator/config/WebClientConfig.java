package com.seatunnel.orchestrator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

  private final OrchestrationProperties orchestrationProperties;

  @Bean
  public WebClient webClient(WebClient.Builder builder) {
    return builder
      .defaultHeaders(headers -> headers.setBasicAuth(
        orchestrationProperties.getEtlServiceUsername(),
        orchestrationProperties.getEtlServicePassword()))
      .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB buffer
      .build();
  }
}