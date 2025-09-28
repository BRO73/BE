package com.example.restaurant_management.dto.request;

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
public class TransactionRequest {
    private Long orderId;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private LocalDateTime transactionTime;
    private String transactionCode;
    private Long cashierId;
}
