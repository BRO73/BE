package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.response.OrderResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<OrderResponse> getAllOrders();
    Optional<OrderResponse> getOrderById(Long id);
    OrderResponse createOrder(OrderRequest request);
    OrderResponse updateOrder(Long id, OrderRequest request);
    void deleteOrder(Long id);
    List<OrderResponse> getOrdersByTable(Long tableId);
    List<OrderResponse> getOrdersByStatus(String status);
    List<OrderResponse> getOrdersByStaff(Long staffId);
    List<OrderResponse> getOrdersBetween(LocalDateTime start, LocalDateTime end);
}
