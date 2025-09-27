package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.RegisterRequest;
import com.example.restaurant_management.dto.request.SignInRequest;
import com.example.restaurant_management.dto.response.RestaurantResponse;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<RestaurantResponse<TokenResponse>> login(@RequestBody SignInRequest request) {
        TokenResponse token = authenticationService.authenticate(request);
        return RestaurantResponse.ok(token, "Login successful");
    }

    @PostMapping("/register")
    public ResponseEntity<RestaurantResponse<String>> register(@RequestBody RegisterRequest request) {
        String message = authenticationService.register(request);
        return RestaurantResponse.ok(message, "Register successful");
    }
}
