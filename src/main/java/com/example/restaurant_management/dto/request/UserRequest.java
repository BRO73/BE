package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
public record UserRequest(
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
        String phoneNumber,

        @NotBlank
        String role,

        @NotBlank(message = "Store name must not be blank")
        String storeName
) {}
