package com.example.restaurant_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MenuItemResponse1(
    String name,
    String imageUrl,
    BigDecimal price,
    String status,
    Long categoryId
){}
