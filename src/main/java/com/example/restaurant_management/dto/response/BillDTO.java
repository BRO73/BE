package com.example.restaurant_management.dto.response;

import java.math.BigDecimal;
public record BillDTO(
        Long billId,
        BigDecimal totalAmount,
        String status
) {}
