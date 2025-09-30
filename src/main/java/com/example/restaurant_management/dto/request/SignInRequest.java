package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest (

        @NotBlank(message = "Username must not be blank")
        String username,

        @NotBlank(message = "Password must not be blank")
        String password,

        @NotBlank(message = "Store name must not be blank")
        String storeName

)
{}

