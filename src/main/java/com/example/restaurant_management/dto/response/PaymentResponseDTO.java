package com.example.restaurant_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private String transactionCode;
    private String checkoutUrl;
    private String paymentStatus;
    private Long orderId;
}
