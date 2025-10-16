package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.OrderDetailRequest;
import com.example.restaurant_management.dto.response.OrderDetailResponse;

import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    List<OrderDetailResponse> getAllOrderDetails();
    Optional<OrderDetailResponse> getOrderDetailById(Long id);
    OrderDetailResponse createOrderDetail(Long orderId, OrderDetailRequest request);
    OrderDetailResponse updateOrderDetail(Long id, OrderDetailRequest request);
    void deleteOrderDetail(Long id);
    List<OrderDetailResponse> getOrderDetailsByOrder(Long orderId);
    List<OrderDetailResponse> getOrderDetailsByMenuItem(Long menuItemId);
    List<OrderDetailResponse> getOrderDetailsByStatus(String status);
}
