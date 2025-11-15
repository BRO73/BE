package com.example.restaurant_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashPaymentResponse {
    private Long transactionId;
    private Long orderId;
    private String orderNumber;
    private BigDecimal amountPaid;
    private BigDecimal amountOriginal;
    private BigDecimal discountAmount;
    private String promotionCode;
    private String paymentMethod;
    private String paymentStatus;
    private LocalDateTime transactionTime;
    private String message;
}