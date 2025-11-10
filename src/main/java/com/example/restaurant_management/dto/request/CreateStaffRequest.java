package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStaffRequest(
        @NotNull(message = "User ID must not be null")
        Long userId,

        @NotBlank(message = "Full name must not be blank")
        String fullName,

        @Email(message = "Email should be valid")
        @NotBlank(message = "Email must not be blank")
        String email,

        @NotBlank(message = "Phone number must not be blank")
        String phoneNumber
) {}
