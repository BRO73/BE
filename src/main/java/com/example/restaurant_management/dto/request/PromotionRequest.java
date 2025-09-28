package com.example.restaurant_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionRequest {
    private String name;
    private String code;
    private String description;
    private String promotionType;
    private BigDecimal value;
    private BigDecimal minSpend;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
}
