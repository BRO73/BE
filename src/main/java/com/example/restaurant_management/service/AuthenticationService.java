package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.RegisterRequest;
import com.example.restaurant_management.dto.request.SignInRequest;
import com.example.restaurant_management.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse authenticate(SignInRequest request);

    String register(RegisterRequest request);
}
