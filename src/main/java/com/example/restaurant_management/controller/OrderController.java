package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.AddItemsRequest;
import com.example.restaurant_management.dto.request.LinkCustomerRequest;
import com.example.restaurant_management.dto.request.MergeOrderRequest;
import com.example.restaurant_management.dto.request.OrderRequest;
import com.example.restaurant_management.dto.response.OrderResponse;
import com.example.restaurant_management.dto.response.SplitOrderRequest;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.service.OrderService;
import com.example.restaurant_management.service.TableTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final TableTokenService tableTokenService;


    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
                                                     Authentication authentication) {
        return ResponseEntity.ok(orderService.createOrder(request, authentication));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.updateOrder(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getOrdersByTable(tableId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStaff(@PathVariable Long staffId) {
        return ResponseEntity.ok(orderService.getOrdersByStaff(staffId));
    }

    @GetMapping("/between")
    public ResponseEntity<List<OrderResponse>> getOrdersBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(orderService.getOrdersBetween(start, end));
    }

    @GetMapping("/table/{tableId}/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrdersByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(orderService.getActiveOrdersByTable(tableId));
    }

    @PostMapping("/{orderId}/add-items")
    public ResponseEntity<OrderResponse> addItemsToOrder(
            @PathVariable Long orderId,
            @Valid @RequestBody AddItemsRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.addItemsToOrder(orderId, request, authentication));
    }

    @PutMapping("/{orderId}/link-customer")
    public ResponseEntity<OrderResponse> linkCustomer(
            @PathVariable Long orderId,
            @Valid @RequestBody LinkCustomerRequest request) {

        OrderResponse updatedOrder = orderService.linkCustomerToOrder(orderId, request.getUserId());

        return ResponseEntity.ok(updatedOrder);
    }

    @PutMapping("/{orderId}/unlink-customer")
    public ResponseEntity<OrderResponse> unlinkCustomer(@PathVariable Long orderId) {
        OrderResponse response = orderService.unlinkCustomer(orderId);
        return ResponseEntity.ok(response);
    }
      
    @GetMapping("/table-token/{token}/active")
    public ResponseEntity<List<OrderResponse>> getActiveOrdersByTableToken(@PathVariable String token) {
        Long tableId = tableTokenService.resolveTableId(token);
        return ResponseEntity.ok(orderService.getActiveOrdersByTable(tableId));
    }

    @PostMapping("/merge")
    public ResponseEntity<OrderResponse> mergeOrders(@Valid @RequestBody MergeOrderRequest request) {

        OrderResponse response = orderService.mergeOrders(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/split")
    public ResponseEntity<OrderResponse> splitOrder(@Valid @RequestBody SplitOrderRequest request) {

        OrderResponse response = orderService.splitOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
