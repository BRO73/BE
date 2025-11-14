// CashPaymentRequestDTO.java
package com.example.restaurant_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CashPaymentRequestDTO {
    private Long orderId;
    private Long amountReceived; // Số tiền khách đưa (optional, để tracking)
}