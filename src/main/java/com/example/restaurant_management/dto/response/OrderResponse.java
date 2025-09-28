package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.Order;
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
public class OrderResponse {
    private Long id;
    private Long tableId;
    private String tableNumber;
    private Long staffId;
    private String staffName;
    private BigDecimal totalAmount;
    private String status;
    private String notes;
    private LocalDateTime completedAt;
    private Long promotionId;
    private String promotionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .tableId(order.getTable().getId())
                .tableNumber(order.getTable().getTableNumber())
                .staffId(order.getStaff() != null ? order.getStaff().getId() : null)
                .staffName(order.getStaff() != null ? order.getStaff().getFullName() : null)
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .notes(order.getNotes())
                .completedAt(order.getCompletedAt())
                .promotionId(order.getPromotion() != null ? order.getPromotion().getId() : null)
                .promotionName(order.getPromotion() != null ? order.getPromotion().getName() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .deleted(order.isDeleted())
                .activated(order.isActivated())
                .build();
    }
}
