package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.RegisterCustomerRequest;
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

    // ✅ Firebase flow: client gửi idToken (Firebase) để backend verify và phát hành JWT nội bộ
    @PostMapping("/verify-firebase")
    public ResponseEntity<TokenResponse> verifyWithFirebase(@RequestParam String idToken) {
        return ResponseEntity.ok(customerAuthService.verifyFirebaseIdToken(idToken));
    }

    // đăng ký thông tin profile sau khi đã xác thực số điện thoại
    @PostMapping("/register-customer")
    public ResponseEntity<TokenResponse> registerCustomer(@RequestBody RegisterCustomerRequest request) {
        return ResponseEntity.ok(customerAuthService.registerCustomer(request));
    }
}
