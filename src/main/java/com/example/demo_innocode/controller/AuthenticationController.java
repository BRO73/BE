package com.example.demo_innocode.controller;

import com.example.demo_innocode.dto.request.RegisterRequest;
import com.example.demo_innocode.dto.request.SignInRequest;
import com.example.demo_innocode.dto.response.TokenResponse;
import com.example.demo_innocode.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody SignInRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authenticationService.register(request));
    }
}
