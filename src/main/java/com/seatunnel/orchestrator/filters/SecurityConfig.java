package com.seatunnel.orchestrator.filters;

import com.seatunnel.orchestrator.config.OrchestrationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final OrchestrationProperties properties;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
    http
      .csrf().disable()
      .authorizeHttpRequests()
      .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
      .anyRequest().authenticated()
      .and()
      .addFilterBefore(jwtFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }


  @Bean
  public UserDetailsService users() {
    return new InMemoryUserDetailsManager(
      User.withDefaultPasswordEncoder()
        .username(properties.getUsername())
        .password(properties.getPassword())
        .roles("ADMIN")
        .build()
    );
  }
}