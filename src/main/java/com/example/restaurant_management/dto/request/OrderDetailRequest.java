package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequest {
    @NotNull
    private Long menuItemId;

    @NotNull
    private Integer quantity;

    private String specialRequirements;
}
