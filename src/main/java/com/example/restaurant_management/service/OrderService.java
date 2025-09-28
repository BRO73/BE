package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> getAllOrders();
    Optional<Order> getOrderById(Long id);
    Order createOrder(Order order);
    Order updateOrder(Long id, Order order);
    void deleteOrder(Long id);
    List<Order> getOrdersByTable(Long tableId);
    List<Order> getOrdersByStatus(String status);
    List<Order> getOrdersByStaff(Long staffId);
    List<Order> getOrdersBetween(LocalDateTime start, LocalDateTime end);
}
