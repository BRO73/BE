package com.example.restaurant_management.controller;

import com.example.restaurant_management.service.TableTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qrcode")
@RequiredArgsConstructor
public class TableQRCodeController {

    private final TableTokenService tokenService;

    /**
     * API sinh token hashed từ tableId thật
     * Ví dụ: GET /api/qrcode/generate?tableId=5
     * → trả về t1_Fjs8n2kA91kLmQ
     */
    @GetMapping("/generate")
    public String generate(@RequestParam Long tableId) {
        return tokenService.hashTableId(tableId);
    }

    /**
     * API để test: resolve token ngược lại thành tableId thật
     * Ví dụ: GET /api/qrcode/resolve?token=t1_Fjs8n2kA91kLmQ
     * → trả về 5
     */
    @GetMapping("/resolve")
    public Long resolve(@RequestParam String token) {
        return tokenService.resolveTableId(token);
    }
}
