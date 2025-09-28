package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.OrderDetail;
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
public class OrderDetailResponse {
    private Long id;
    private Long orderId;
    private Long menuItemId;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal priceAtOrder;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

    public static OrderDetailResponse fromEntity(OrderDetail orderDetail) {
        return OrderDetailResponse.builder()
                .id(orderDetail.getId())
                .orderId(orderDetail.getOrder().getId())
                .menuItemId(orderDetail.getMenuItem().getId())
                .menuItemName(orderDetail.getMenuItem().getName())
                .quantity(orderDetail.getQuantity())
                .priceAtOrder(orderDetail.getPriceAtOrder())
                .status(orderDetail.getStatus())
                .notes(orderDetail.getNotes())
                .createdAt(orderDetail.getCreatedAt())
                .updatedAt(orderDetail.getUpdatedAt())
                .deleted(orderDetail.isDeleted())
                .activated(orderDetail.isActivated())
                .build();
    }
}
