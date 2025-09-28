package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.OrderDetailRepository;
import com.example.restaurant_management.service.OrderDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    @Override
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    @Override
    public Optional<OrderDetail> getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id);
    }

    @Override
    public OrderDetail createOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetail orderDetail) {
        orderDetail.setId(id);
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrder(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByMenuItem(Long menuItemId) {
        return orderDetailRepository.findByMenuItemId(menuItemId);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByStatus(String status) {
        return orderDetailRepository.findByStatus(status);
    }
}
