package com.example.restaurant_management.dto.response;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SplitOrderRequest {

    @NotNull(message = "Source order ID is required")
    private Long sourceOrderId; // Order gốc

    @NotNull(message = "New table ID is required")
    private Long newTableId; // Bàn mới cho order split ra

    @NotEmpty(message = "Split items cannot be empty")
    private Map<Long, Integer> splitItems; // Key: orderDetailId, Value: quantityToSplit
}
