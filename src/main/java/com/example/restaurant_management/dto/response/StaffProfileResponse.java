package com.example.restaurant_management.dto.response;

import lombok.Builder;

import java.util.Set;

@Builder
public record StaffProfileResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String phoneNumber,
        Set<String> roles
) {}
