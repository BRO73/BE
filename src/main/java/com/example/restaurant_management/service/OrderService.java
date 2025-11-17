package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.AddItemsRequest;
import com.example.restaurant_management.dto.request.MergeOrderRequest;
import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.response.OrderResponse;
import com.example.restaurant_management.dto.response.SplitOrderRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    List<OrderResponse> getAllOrders();
    OrderResponse getOrderById(Long id);
    OrderResponse createOrder(OrderRequest request, Authentication authentication);
    OrderResponse updateOrder(Long id, OrderRequest request);
    void deleteOrder(Long id);
    List<OrderResponse> getOrdersByTable(Long tableId);
    List<OrderResponse> getOrdersByStatus(String status);
    List<OrderResponse> getOrdersByStaff(Long staffId);
    List<OrderResponse> getOrdersBetween(LocalDateTime start, LocalDateTime end);

    List<OrderResponse> getActiveOrdersByTable(Long tableId);

    OrderResponse addItemsToOrder(Long orderId, @Valid AddItemsRequest request, Authentication authentication);

    OrderResponse linkCustomerToOrder(Long orderId, @NotNull(message = "customerId không được để trống") Long customerId);

    OrderResponse unlinkCustomer(Long orderId);

    OrderResponse mergeOrders(@Valid MergeOrderRequest request);

    OrderResponse splitOrder(@Valid SplitOrderRequest request);

    List<OrderResponse> getOrdersByStatusAndOrderDetailsCompleted(String status);
}
