package com.example.restaurant_management.dto.request;

public record SplitItemRequest(
        Long menuItemId,
        double quantity // Dùng double để cho phép tách 0.5 Phở
) {}
