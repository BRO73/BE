package com.example.restaurant_management.controller;

import com.example.restaurant_management.model.OrderInfo;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class OrderController1 {

    // Khi khách hàng đặt món
    @MessageMapping("/new-order")
    @SendTo("/topic/orders")
    public OrderInfo newOrder(OrderInfo order) {
        order.setStatus("Đang chế biến");
        System.out.println("🍽️ Nhận order mới: " + order);
        return order;
    }

    // Khi bếp cập nhật trạng thái món
    @MessageMapping("/update-status")
    @SendTo("/topic/order-status")
    public OrderInfo updateOrderStatus(OrderInfo order) {
        System.out.println("🔄 Cập nhật trạng thái: " + order);
        return order;
    }
}
