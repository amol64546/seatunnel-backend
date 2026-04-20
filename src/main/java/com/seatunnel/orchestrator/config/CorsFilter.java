
package com.seatunnel.orchestrator.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class CorsFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
    String query = Optional.ofNullable(request.getQueryString()).map(s -> "?" + s).orElse("");
    log.info("[{}] : {}{}", request.getMethod(), request.getRequestURL(), query);

    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN,
      request.getHeader(HttpHeaders.ORIGIN));
    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, DELETE, PUT");
    response.setHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");
    response.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,
      "Content-Type, Accept, X-Requested-With, remember-me, Authorization");

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
