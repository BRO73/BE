package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.OrderDetailRequest;
import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.response.OrderDetailResponse;
import com.example.restaurant_management.dto.response.OrderResponse;
import com.example.restaurant_management.entity.*;
import com.example.restaurant_management.repository.*;
import com.example.restaurant_management.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public Optional<OrderResponse> getOrderById(Long id) {
        return orderRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        User staff = userRepository.findById(request.getStaffId()).orElse(null);
        User customer = userRepository.findById(request.getCustomerId()).orElse(null);

        Order order = Order.builder()
                .table(table)
                .staffUser(staff)
                .customerUser(customer)
                .status(request.getStatus())
                .notes(request.getNotes())
                .totalAmount(BigDecimal.ZERO)
                .build();

        order = orderRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        List<OrderDetailResponse> details = new ArrayList<>();

        if (request.getOrderDetails() != null) {
            for (OrderDetailRequest d : request.getOrderDetails()) {
                MenuItem item = menuItemRepository.findById(d.getMenuItemId())
                        .orElseThrow(() -> new RuntimeException("Menu item not found"));

                BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
                total = total.add(lineTotal);

                OrderDetail od = OrderDetail.builder()
                        .order(order)
                        .menuItem(item)
                        .quantity(d.getQuantity())
                        .priceAtOrder(item.getPrice())
                        .status("Pending")
                        .notes(d.getNotes())
                        .build();
                od = orderDetailRepository.save(od);
                details.add(toDetailResponse(od));
            }
        }

        order.setTotalAmount(total);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return toResponse(order, details);
    }

    @Transactional
    @Override
    public OrderResponse updateOrder(Long id, OrderRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setNotes(request.getNotes());
        order.setStatus(request.getStatus());
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return toResponse(order);
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public List<OrderResponse> getOrdersByTable(Long tableId) {
        return orderRepository.findByTableId(tableId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status).stream().map(this::toResponse).toList();
    }

    @Override
    public List<OrderResponse> getOrdersByStaff(Long staffId) {
        return orderRepository.findByStaffUserId(staffId).stream().map(this::toResponse).toList();
    }

    @Override
    public List<OrderResponse> getOrdersBetween(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByCreatedAtBetween(start, end).stream().map(this::toResponse).toList();
    }

    // ✅ Mapper thủ công
    private OrderResponse toResponse(Order order) {
        List<OrderDetailResponse> details = orderDetailRepository.findByOrderId(order.getId())
                .stream().map(this::toDetailResponse).toList();
        return toResponse(order, details);
    }

    private OrderResponse toResponse(Order order, List<OrderDetailResponse> details) {
        return OrderResponse.builder()
                .id(order.getId())
                .tableId(order.getTable() != null ? order.getTable().getId() : null)
                .staffId(order.getStaffUser() != null ? order.getStaffUser().getId() : null)
                .customerId(order.getCustomerUser() != null ? order.getCustomerUser().getId() : null)
                .promotionId(order.getPromotion() != null ? order.getPromotion().getId() : null)
                .status(order.getStatus())
                .notes(order.getNotes())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderDetails(details)
                .build();
    }

    private OrderDetailResponse toDetailResponse(OrderDetail d) {
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
