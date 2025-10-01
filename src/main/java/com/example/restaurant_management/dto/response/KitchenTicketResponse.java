package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.OrderDetail;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenTicketResponse {
    private Long orderDetailId;
    private Long orderId;
    private String tableNumber;
    private Long menuItemId;
    private String dishName;
    private Integer quantity;
    private String menuStatus;
    private String status;
    private String notes;
    private LocalDateTime orderedAt;

    public static KitchenTicketResponse from(OrderDetail od) {
        var order = od.getOrder();
        var menu  = od.getMenuItem();

        LocalDateTime orderedLocal = od.getCreatedAt(); // <-- FIX

        return KitchenTicketResponse.builder()
                .orderDetailId(od.getId())
                .orderId(order != null ? order.getId() : null)
                .tableNumber(order != null && order.getTable() != null ? order.getTable().getTableNumber() : null)
                .menuItemId(menu != null ? menu.getId() : null)
                .dishName(menu != null ? menu.getName() : null)
                .quantity(od.getQuantity())
                .menuStatus(menu != null ? menu.getStatus() : null)
                .status(od.getStatus())
                .notes(od.getNotes())
                .orderedAt(orderedLocal)
                .build();
    }
}
