package com.example.restaurant_management.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.Authentication;

import java.util.Map;
import java.util.function.Function;

public interface JWTService {
    public String generateToken(Authentication authentication);

    public String generateToken(Map<String, Object> claims, Authentication authentication);

    public String generateRefreshToken(Map<String, Object> claims, Authentication authentication);
    public String extractUserName(String token);

    boolean isTokenValid(String token, Authentication authentication);

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver);
}
