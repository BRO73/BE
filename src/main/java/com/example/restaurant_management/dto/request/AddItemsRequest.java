package com.example.restaurant_management.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddItemsRequest {

    @NotEmpty(message = "Danh sách món không được rỗng")
    @Valid
    private List<OrderDetailItem> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderDetailItem {

        @NotNull(message = "MenuItemId không được null")
        @Positive(message = "MenuItemId phải là số dương")
        private Long menuItemId;

        @NotNull(message = "Quantity không được null")
        @Positive(message = "Quantity phải lớn hơn 0")
        private Integer quantity;

        private String specialRequirements;
    }
}