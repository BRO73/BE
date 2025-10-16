package com.example.restaurant_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long tableId;
    private Long staffId;
    private Long customerId;
    private Long promotionId;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private List<OrderDetailRequest> orderDetails;
}
