package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UpdateUserRequest(
        @NotBlank(message = "Username must not be blank")
        String username,

        @NotBlank
        @Size(min = 5, message = "Password must be at least 5 characters long")
        String password,

        @NotBlank
        Set<String> role
) {}
