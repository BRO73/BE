package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.OrderSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderSessionRepository extends JpaRepository<OrderSession, Long> {
}
