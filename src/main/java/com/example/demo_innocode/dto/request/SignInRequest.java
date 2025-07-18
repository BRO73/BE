package com.example.demo_innocode.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest (

        @NotBlank(message = "Username must not be blank")
        String username,

        @NotBlank(message = "Password must not be blank")
        String password
){}
