package com.example.restaurant_management.dto.response;

import lombok.Builder;

@Builder
public record TokenResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn,
        String registrationToken
){}
