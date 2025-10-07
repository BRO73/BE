package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.RegisterCustomerRequest;
import com.example.restaurant_management.dto.response.OtpLoginResponse;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.service.impl.CustomerAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class OtpController {

    private final CustomerAuthService customerAuthService;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String phoneNumber) {
        customerAuthService.sendOtp(phoneNumber);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<TokenResponse> verifyOtp(@RequestParam String phoneNumber,
                                                      @RequestParam String otp) {
        return ResponseEntity.ok(customerAuthService.verifyOtp(phoneNumber, otp));
    }

    @PostMapping("/register-customer")
    public ResponseEntity<TokenResponse> registerCustomer(@RequestBody RegisterCustomerRequest request) {
        return ResponseEntity.ok(customerAuthService.registerCustomer(request));
    }
}
