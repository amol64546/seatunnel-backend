package com.seatunnel.orchestrator.controller;

import com.seatunnel.orchestrator.dto.LoginRequest;
import com.seatunnel.orchestrator.dto.LoginResponse;
import com.seatunnel.orchestrator.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtUtil jwtUtil;

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    // Authenticate user (replace with real authentication)
    if ("admin".equals(request.getUsername()) && "admin".equals(request.getPassword())) {
      String token = jwtUtil.generateToken(request.getUsername());
      String refreshToken = jwtUtil.generateRefreshToken(request.getUsername());
      long expiresIn = 60 * 60; // 1 hour in seconds
      return ResponseEntity.ok(new LoginResponse(token, expiresIn, refreshToken));
    }
    return ResponseEntity.status(401).build();

  }

  @PostMapping("/refresh")
  public ResponseEntity<LoginResponse> refresh(@RequestParam String refreshToken) {
    try {
      Claims claims = jwtUtil.extractAllClaims(refreshToken);
      String username = claims.getSubject();
      String token = jwtUtil.generateToken(username);
      long expiresIn = 60 * 60; // 1 hour in seconds
      String newRefreshToken = jwtUtil.generateRefreshToken(username);
      return ResponseEntity.ok(new LoginResponse(token, expiresIn, newRefreshToken));
    } catch (Exception e) {
      return ResponseEntity.status(401).build();
    }
  }
}