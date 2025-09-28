package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.Promotion;
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
public class PromotionResponse {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String promotionType;
    private BigDecimal value;
    private BigDecimal minSpend;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer usageLimit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

    public static PromotionResponse fromEntity(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .promotionType(promotion.getPromotionType())
                .value(promotion.getValue())
                .minSpend(promotion.getMinSpend())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .usageLimit(promotion.getUsageLimit())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .deleted(promotion.isDeleted())
                .activated(promotion.isActivated())
                .build();
    }
}
