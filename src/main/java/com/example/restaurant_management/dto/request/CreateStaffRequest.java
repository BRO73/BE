package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateStaffRequest(

        @NotBlank(message = "Full name is required.")
        String fullName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email should be valid.")
        String email,

        @NotBlank
        String phoneNumber

) {}
