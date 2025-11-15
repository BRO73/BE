package com.example.restaurant_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long orderId;
    private String returnUrl;
    private String cancelUrl;
    private String promotionCode;
}
