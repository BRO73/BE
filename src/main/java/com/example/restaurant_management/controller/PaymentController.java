package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.PaymentRequestDTO;
import com.example.restaurant_management.dto.response.PaymentResponseDTO;
import com.example.restaurant_management.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDTO> createPayment(@RequestBody PaymentRequestDTO request) {
        PaymentResponseDTO response = paymentService.createPaymentLink(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Map<String, Object> webhookData) {
        paymentService.handlePaymentWebhook(webhookData);
        return ResponseEntity.ok("Webhook processed successfully");
    }
}
