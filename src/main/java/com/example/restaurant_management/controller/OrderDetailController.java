package com.example.restaurant_management.controller;

import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.service.OrderDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-details")
@PreAuthorize("hasAnyRole('ADMIN')")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDetail>> getAllOrderDetails() {
        return ResponseEntity.ok(orderDetailService.getAllOrderDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetail> getOrderDetailById(@PathVariable Long id) {
        return orderDetailService.getOrderDetailById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<OrderDetail> createOrderDetail(@RequestBody OrderDetail orderDetail) {
        return ResponseEntity.ok(orderDetailService.createOrderDetail(orderDetail));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDetail> updateOrderDetail(@PathVariable Long id, @RequestBody OrderDetail orderDetail) {
        return ResponseEntity.ok(orderDetailService.updateOrderDetail(id, orderDetail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable Long id) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByOrder(orderId));
    }

    @GetMapping("/menu-item/{menuItemId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByMenuItem(@PathVariable Long menuItemId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByMenuItem(menuItemId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByStatus(status));
    }
}
