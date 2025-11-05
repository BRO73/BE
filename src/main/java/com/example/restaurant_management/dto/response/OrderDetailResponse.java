package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.entity.OrderDetail;

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
    private BigDecimal priceAtOrder;
    private OrderItemStatus status;   // <--- đổi sang enum
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

    public static OrderDetailResponse fromEntity(OrderDetail od) {
        Long safeOrderId = (od.getOrder() != null) ? od.getOrder().getId() : null;
        Long safeMenuItemId = (od.getMenuItem() != null) ? od.getMenuItem().getId() : null;
        String safeMenuItemName = (od.getMenuItem() != null) ? od.getMenuItem().getName() : null;

        return OrderDetailResponse.builder()
                .id(od.getId())
                .orderId(safeOrderId)
                .menuItemId(safeMenuItemId)
                .menuItemName(safeMenuItemName)
                .quantity(od.getQuantity())
                .priceAtOrder(od.getPriceAtOrder())
                .status(od.getStatus())              // enum -> OK
                .notes(od.getNotes())
                .createdAt(od.getCreatedAt())
                .updatedAt(od.getUpdatedAt())
                .deleted(od.isDeleted())
                .activated(od.isActivated())
                .build();
    }
    private BigDecimal price;
    private String specialRequirements;
}
