package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.OrderDetail;
import lombok.*;
import java.time.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KitchenTicketResponse {
    private Long orderDetailId;
    private Long orderId;
    private String tableNumber;
    private Long menuItemId;
    private String dishName;
    private Integer quantity;
    private String status;
    private String notes;
    private LocalDateTime orderedAt;

    private Long elapsedSeconds;
    private Boolean overtime;

    public static KitchenTicketResponse from(OrderDetail od, int overtimeMinutes) {
        var order = od.getOrder();
        var menu  = od.getMenuItem();
        var ordered = od.getCreatedAt();
        long elapsed = (ordered != null)
                ? Duration.between(ordered, LocalDateTime.now()).getSeconds()
                : 0;
        boolean isOver = elapsed >= overtimeMinutes * 60L;

        return KitchenTicketResponse.builder()
                .orderDetailId(od.getId())
                .orderId(order != null ? order.getId() : null)
                .tableNumber(order != null && order.getTable() != null ? order.getTable().getTableNumber() : null)
                .menuItemId(menu != null ? menu.getId() : null)
                .dishName(menu != null ? menu.getName() : null)
                .quantity(od.getQuantity())
                .status(od.getStatus())
                .notes(od.getNotes())
                .orderedAt(ordered)
                .elapsedSeconds(elapsed)
                .overtime(isOver)
                .build();
    }
}
