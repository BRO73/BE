package com.example.restaurant_management.dto.response;

import lombok.Builder;

@Builder
public record UserProfileResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phoneNumber
) {}
