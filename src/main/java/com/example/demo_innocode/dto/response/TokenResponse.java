package com.example.demo_innocode.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn
){}
