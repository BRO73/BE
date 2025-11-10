package com.example.restaurant_management.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JWTServiceImplTest {

    JWTServiceImpl jwtService;

    @BeforeEach
    void setup() {
        jwtService = new JWTServiceImpl();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "dGhpc2lzbXl2ZXJ5c2VjdXJlc3VwZXJzZWNyZXRrZXkxMjM0NTY=");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", "10");
        ReflectionTestUtils.setField(jwtService, "jwtRefreshExpiration", "60");
    }

    @Test
    void generateToken_ShouldReturnValidJwt() {
        // ---------- ARRANGE ----------
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "tuan", "pw", List.of(() -> "ROLE_USER"));

        // ---------- ACT ----------
        String token = jwtService.generateToken(Map.of("k", "v"), auth);

        // ---------- ASSERT ----------
        assertNotNull(token);
        assertEquals("tuan", jwtService.extractUserName(token));
        assertTrue(jwtService.isTokenValid(token, auth));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenDifferentUsername() {
        // ---------- ARRANGE ----------
        Authentication auth = new UsernamePasswordAuthenticationToken("tuan", "x");
        String token = jwtService.generateToken(auth);
        Authentication other = new UsernamePasswordAuthenticationToken("other", "x");

        // ---------- ACT ----------
        boolean result = jwtService.isTokenValid(token, other);

        // ---------- ASSERT ----------
        assertFalse(result);
    }
}
