package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.awt.*;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryId(Long categoryId);
    List<MenuItem> findByStatus(String status);
    List<MenuItem> findByNameContainingIgnoreCase(String name);


    @Query("""
        SELECT od.menuItem 
        FROM OrderDetail od 
        GROUP BY od.menuItem 
        ORDER BY SUM(od.quantity) DESC
        """)
    List<MenuItem> findTop4MostOrderedMenuItems();
}
