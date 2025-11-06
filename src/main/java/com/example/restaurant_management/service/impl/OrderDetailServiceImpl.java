package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.OrderDetailRepository;
import com.example.restaurant_management.service.OrderDetailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrderDetail> getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id);
    }

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetail orderDetail) {
        orderDetail.setId(id);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetail> getOrderDetailsByOrder(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetail> getOrderDetailsByMenuItem(Long menuItemId) {
        return orderDetailRepository.findByMenuItemId(menuItemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDetail> getOrderDetailsByStatus(String status) {
        // Map String -> Enum để đồng bộ kiểu dữ liệu (tránh dùng method String deprecated)
        OrderItemStatus st = OrderItemStatus.valueOf(status.trim().toUpperCase());
        return orderDetailRepository.findByStatus(st);
    }
}
