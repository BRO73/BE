package com.example.restaurant_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long id;
    private MenuItemResponse menuItem;
    private Integer quantity;
    private BigDecimal price;
    private String specialRequirements;
}
