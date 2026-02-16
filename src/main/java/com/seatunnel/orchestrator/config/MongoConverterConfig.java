package com.seatunnel.orchestrator.config;

import com.seatunnel.orchestrator.converter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConverterConfig {

  @Bean
  public MongoCustomConversions customConversions() {
    return new MongoCustomConversions(Arrays.asList(
      new EtlBrickReadingConverter(),
      new EtlBrickWritingConverter(),
      new EtlNodeReadingConverter(),
      new EtlNodeWritingConverter(),
      new EtlConfigWritingConverter(),
      new EtlConfigReadingConverter()
    ));
  }
}