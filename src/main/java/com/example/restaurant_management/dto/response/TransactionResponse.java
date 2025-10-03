package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.Transaction;
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
public class TransactionResponse {
    private Long id;
    private Long orderId;
    private BigDecimal amountPaid;
    private String paymentMethod;
    private LocalDateTime transactionTime;
    private String transactionCode;
    private Long cashierId;
    private String cashierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

}
