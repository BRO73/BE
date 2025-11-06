package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.common.enums.MenuItemAvailability;
import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.entity.OrderDetail;
import lombok.*;
import java.time.ZoneId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
    private MenuItemAvailability availability;
    private String status;

    private String notes;
    private String  orderedAt;

    public static KitchenTicketResponse from(OrderDetail od) {
        return KitchenTicketResponse.builder()
                .orderDetailId(od.getId())
                .orderId(od.getOrder() != null ? od.getOrder().getId() : null)
                .tableNumber(
                        od.getOrder() != null && od.getOrder().getTable() != null
                                ? od.getOrder().getTable().getTableNumber()
                                : null
                )
                .menuItemId(od.getMenuItem() != null ? od.getMenuItem().getId() : null)
                .dishName(od.getMenuItem() != null ? od.getMenuItem().getName() : null)
                .quantity(od.getQuantity())
                .availability(od.getMenuItem() != null ? od.getMenuItem().getAvailability() : null)
                .status(od.getStatus())
                .notes(od.getNotes())
                .orderedAt(toIsoUtc(od.getCreatedAt()))
                .build();
    }

    private static String toIsoUtc(LocalDateTime ldt) {
        return ldt == null ? null : ldt.atOffset(ZoneOffset.UTC).toInstant().toString();
    }

}
