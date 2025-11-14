package com.example.restaurant_management.service;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.dto.response.KitchenBoardResponse;

public interface KitchenService {

    KitchenBoardResponse board(int limit);

    void updateMenuAvailability(Long menuItemId, boolean available);

    void updateOrderDetailStatus(Long orderDetailId, String status);

    // NEW: “>” — hoàn thành 1 đơn vị (tách 1 sang DONE)
    void completeOneUnit(Long orderDetailId);

    // NEW: (chờ cung ứng) “>” — xuất 1 đơn vị khỏi DONE
    void serveOneUnit(Long orderDetailId);


    void completeAllUnits(Long id);

    void notifyBoardUpdate();
}
