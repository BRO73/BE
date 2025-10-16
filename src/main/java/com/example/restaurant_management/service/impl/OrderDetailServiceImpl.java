package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.OrderDetailRequest;
import com.example.restaurant_management.dto.response.OrderDetailResponse;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.repository.OrderDetailRepository;
import com.example.restaurant_management.repository.OrderRepository;
import com.example.restaurant_management.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderRepository orderRepository;

    @Override
    public List<OrderDetailResponse> getAllOrderDetails() {
        return orderDetailRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public Optional<OrderDetailResponse> getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id).map(this::toResponse);
    }

    @Override
    public OrderDetailResponse createOrderDetail(Long orderId, OrderDetailRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        MenuItem item = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        OrderDetail detail = OrderDetail.builder()
                .order(order)
                .menuItem(item)
                .quantity(request.getQuantity())
                .priceAtOrder(request.getPriceAtOrder())
                .status(request.getStatus())
                .notes(request.getNotes())
                .build();
        return toResponse(orderDetailRepository.save(detail));
    }

    @Override
    public OrderDetailResponse updateOrderDetail(Long id, OrderDetailRequest request) {
        OrderDetail detail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order detail not found"));

        detail.setQuantity(request.getQuantity());
        detail.setPriceAtOrder(request.getPriceAtOrder());
        detail.setStatus(request.getStatus());
        detail.setNotes(request.getNotes());
        return toResponse(orderDetailRepository.save(detail));
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsByOrder(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsByMenuItem(Long menuItemId) {
        return orderDetailRepository.findByMenuItemId(menuItemId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<OrderDetailResponse> getOrderDetailsByStatus(String status) {
        return orderDetailRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    private OrderDetailResponse toResponse(OrderDetail d) {
        return OrderDetailResponse.builder()
                .id(d.getId())
                .orderId(d.getOrder().getId())
                .menuItemId(d.getMenuItem().getId())
                .menuItemName(d.getMenuItem().getName())
                .quantity(d.getQuantity())
                .priceAtOrder(d.getPriceAtOrder())
                .status(d.getStatus())
                .notes(d.getNotes())
                .createdAt(d.getCreatedAt())
                .updatedAt(d.getUpdatedAt())
                .build();
    }
}
