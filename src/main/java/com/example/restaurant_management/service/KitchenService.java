package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.response.KitchenBoardResponse;

public interface KitchenService {
    KitchenBoardResponse board(int limit);
    void updateMenuAvailability(Long menuItemId, boolean available);

}
