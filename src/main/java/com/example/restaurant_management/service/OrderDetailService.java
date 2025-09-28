package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.OrderDetail;

import java.util.List;
import java.util.Optional;

public interface OrderDetailService {
    List<OrderDetail> getAllOrderDetails();
    Optional<OrderDetail> getOrderDetailById(Long id);
    OrderDetail createOrderDetail(OrderDetail orderDetail);
    OrderDetail updateOrderDetail(Long id, OrderDetail orderDetail);
    void deleteOrderDetail(Long id);
    List<OrderDetail> getOrderDetailsByOrder(Long orderId);
    List<OrderDetail> getOrderDetailsByMenuItem(Long menuItemId);
    List<OrderDetail> getOrderDetailsByStatus(String status);
}
