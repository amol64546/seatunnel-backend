package com.seatunnel.orchestrator.util;

import com.seatunnel.orchestrator.config.OrchestrationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {
  private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
  private final OrchestrationProperties properties;

  public String generateToken(String username) {
    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
      .signWith(SignatureAlgorithm.HS256, properties.getJwtSecret())
      .compact();
  }

  public String generateRefreshToken(String username) {
    return Jwts.builder()
      .setSubject(username)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
      .signWith(SignatureAlgorithm.HS256, properties.getJwtSecret())
      .compact();
  }

  public Claims extractAllClaims(String token) {
    return Jwts.parser()
      .setSigningKey(properties.getJwtSecret())
      .parseClaimsJws(token)
      .getBody();
  }
}