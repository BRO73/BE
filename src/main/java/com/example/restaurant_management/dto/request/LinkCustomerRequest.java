package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkCustomerRequest {

    /**
     * Đây là ID của USER (lấy từ CustomerResponse.userId)
     */
    @NotNull(message = "userId không được để trống")
    private Long userId;
}
