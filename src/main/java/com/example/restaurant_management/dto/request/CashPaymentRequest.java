package com.example.restaurant_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CashPaymentRequest {
    private Long orderId;
    private BigDecimal amountReceived; // Số tiền sau khi đã trừ discount
    private String promotionCode; // Có thể null nếu không dùng mã
}
