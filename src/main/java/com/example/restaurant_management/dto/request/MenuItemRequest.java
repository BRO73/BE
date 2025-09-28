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
public class MenuItemRequest {
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String status;
    private Long categoryId;
}
