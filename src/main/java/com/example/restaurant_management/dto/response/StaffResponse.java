package com.example.restaurant_management.dto.response;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record StaffResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        boolean isActivated,
        LocalDateTime createdAt,
        String storeName,
        Long userId
) {}
