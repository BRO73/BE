package com.example.restaurant_management.dto.request;

public record UpdateProfileRequest(
        String fullName,
        String email,
        String phoneNumber
) {}
