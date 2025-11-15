package com.example.restaurant_management.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Data
public class ValidatePromotionRequest {

    @NotEmpty(message = "Mã giảm giá không được để trống")
    private String code;

    @NotNull(message = "Tổng tiền không được để trống")
    private BigDecimal totalAmount;

    private Long userId;
}
