package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.CashPaymentRequest;
import com.example.restaurant_management.dto.request.PaymentRequestDTO;
import com.example.restaurant_management.dto.response.CashPaymentResponse;
import com.example.restaurant_management.dto.response.PaymentResponseDTO;

import java.util.Map;

public interface PaymentService {
    PaymentResponseDTO createPaymentLink(PaymentRequestDTO request);
    void handlePaymentWebhook(Map<String, Object> webhookData);

    CashPaymentResponse processCashPayment(CashPaymentRequest request);
}
