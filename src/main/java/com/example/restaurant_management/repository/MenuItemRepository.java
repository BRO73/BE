package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryId(Long categoryId);
    List<MenuItem> findByStatus(String status);
    List<MenuItem> findByNameContainingIgnoreCase(String name);
}
