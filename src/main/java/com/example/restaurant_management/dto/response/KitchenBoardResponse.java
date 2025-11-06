package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.service.KitchenService;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenBoardResponse {
    private Long menuItemId;
    private String dishName;
    private Integer totalQuantity;
    private String status;
    private boolean overtime;
    private String  serverTime;                 // <-- thêm field này
    private List<KitchenTicketResponse> items;

    public static KitchenBoardResponse from(List<OrderDetail> details) {
        OrderDetail sample = details.get(0);
        int sum = details.stream().mapToInt(OrderDetail::getQuantity).sum();
        return KitchenBoardResponse.builder()
                .menuItemId(sample.getMenuItem() != null ? sample.getMenuItem().getId() : null)
                .dishName(sample.getMenuItem() != null ? sample.getMenuItem().getName() : null)
                .totalQuantity(sum)
                .status(sample.getStatus()) // lấy status hiện tại
                .overtime(false) // hoặc tính toán từ orderedAt
                .build();
    }
}
