package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTServiceImpl implements JWTService {

    @Value("${jwt.secretkey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private String jwtExpiration;

    @Value("${jwt.refresh.expiration}")
    private String jwtRefreshExpiration;

    @Override
    public String generateToken(Authentication authentication) {
        return generateToken(new HashMap<>(), authentication);
    }

    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, Authentication authentication) {
        final String userName = extractUserName(token);
        return userName.equals(authentication.getName());
    }

    @Override
    public String generateToken(Map<String, Object> claims, Authentication authentication) {
        long expiration = Long.parseLong(jwtExpiration) * 1000 * 60;
        return buildToken(claims, authentication, expiration);
    }

    @Override
    public String generateRefreshToken(Map<String, Object> claims, Authentication authentication) {
        long refreshExpiration = Long.parseLong(jwtRefreshExpiration) * 1000 * 60;
        return buildToken(claims, authentication, refreshExpiration);
    }


    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaim(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            Authentication authentication,
            long expiration
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(String.valueOf(authentication.getPrincipal()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
