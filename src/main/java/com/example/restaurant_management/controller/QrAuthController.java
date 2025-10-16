package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.service.QrAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class QrAuthController {

    private final QrAuthService qrAuthService;

    @GetMapping("/qr-login")
    public ResponseEntity<TokenResponse> qrLogin(@RequestParam Long tableId) {
        return ResponseEntity.ok(qrAuthService.qrLogin(tableId));
    }
}
