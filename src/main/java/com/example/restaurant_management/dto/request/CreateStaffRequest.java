package com.example.restaurant_management.dto.request;

import com.example.restaurant_management.common.enums.StaffRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStaffRequest(

        @NotBlank(message = "Full name is required.")
        String fullName,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email should be valid.")
        String email,

        @NotBlank(message = "Phone number is required.")
        String phoneNumber,

        @NotNull(message = "Store is required.")
        Long storeId,

        // Role nghiệp vụ của staff (WAITER, CHEF, CASHIER, ADMIN...)
        @NotNull(message = "Role is required.")
        StaffRole role
) {}
