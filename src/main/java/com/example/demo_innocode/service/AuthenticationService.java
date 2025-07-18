package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.request.RegisterRequest;
import com.example.demo_innocode.dto.request.SignInRequest;
import com.example.demo_innocode.dto.response.TokenResponse;

public interface AuthenticationService {

    TokenResponse authenticate(SignInRequest request);

    String register(RegisterRequest request);
}
