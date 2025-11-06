package com.example.restaurant_management.dto.request;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateOrderDetailStatusRequest {

    /**
     * Hợp lệ: PENDING, IN_PROGRESS, DONE, CANCELED
     */
    @NotNull
    private String status;
}
