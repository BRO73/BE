package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByTableId(Long tableId);
    List<Order> findByStatus(String status);
    List<Order> findByStaffUserId(Long userId);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
