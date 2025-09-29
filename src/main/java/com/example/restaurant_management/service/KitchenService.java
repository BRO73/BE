package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.response.KitchenBoardResponse;

public interface KitchenService {
    KitchenBoardResponse board(int limitPerColumn);
    void updateStatus(Long orderDetailId, String nextStatus);
}
