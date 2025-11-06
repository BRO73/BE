package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserAccountRequest(
        @NotBlank String username,
        String password,   // nếu có => reset
        String role        // MANAGER|WAITER|CHEF|CLEANER|CASHIER (case-insensitive)
) {}
