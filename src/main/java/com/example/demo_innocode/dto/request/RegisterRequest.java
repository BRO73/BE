package com.example.demo_innocode.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username must not be blank")
        String username,

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank
        String fullName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Phone must not be blank")
        String phone,

        @NotBlank(message = "Citizen ID must not be blank")
        String citizenId,

        @NotBlank
        String role
){}
