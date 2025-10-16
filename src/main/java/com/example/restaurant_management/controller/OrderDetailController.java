package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.OrderDetailRequest;
import com.example.restaurant_management.dto.response.OrderDetailResponse;
import com.example.restaurant_management.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN')")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<List<OrderDetailResponse>> getAllOrderDetails() {
        return ResponseEntity.ok(orderDetailService.getAllOrderDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetailById(@PathVariable Long id) {
        return orderDetailService.getOrderDetailById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> createOrderDetail(
            @PathVariable Long orderId,
            @RequestBody OrderDetailRequest request) {
        return ResponseEntity.ok(orderDetailService.createOrderDetail(orderId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> updateOrderDetail(
            @PathVariable Long id,
            @RequestBody OrderDetailRequest request) {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long id) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetailsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByOrder(orderId));
    }

    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetailsByMenuItem(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByMenuItem(menuItemId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetailsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByStatus(status));
    }
}
