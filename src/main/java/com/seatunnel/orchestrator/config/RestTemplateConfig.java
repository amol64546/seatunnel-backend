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

  @Bean
  public RestTemplate restTemplate() {
    log.info("--------bobRestTemplate bean created successfully for master profile-------");
    return new RestTemplate();
  }
}
