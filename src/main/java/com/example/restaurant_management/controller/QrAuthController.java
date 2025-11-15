package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.service.QrAuthService;
import com.example.restaurant_management.service.TableTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class QrAuthController {

    private final QrAuthService qrAuthService;
    private final TableTokenService tableTokenService;

    @GetMapping("/qr-login")
    public ResponseEntity<TokenResponse> qrLogin(
            @RequestParam(required = false) Long tableId,
            @RequestParam(required = false, name = "t") String token
    ) {
        Long id = tableId;
        if (id == null && token != null) {
            id = tableTokenService.resolveTableId(token);
        }
        if (id == null) throw new IllegalArgumentException("Missing tableId or token");
        return ResponseEntity.ok(qrAuthService.qrLogin(id));
    }
}
