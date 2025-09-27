package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.MenuItem;

import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItem> getAllMenuItems();
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItem createMenuItem(MenuItem menuItem);
    MenuItem updateMenuItem(Long id, MenuItem menuItem);
    void deleteMenuItem(Long id);
    List<MenuItem> getMenuItemsByCategory(Long categoryId);
    List<MenuItem> getMenuItemsByStatus(String status);
    List<MenuItem> searchMenuItemsByName(String name);
}
