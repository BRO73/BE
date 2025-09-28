package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateStaffRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @Email(message = "Invalid email")
        String email,

        String phoneNumber,

        String password // optional
) {}

