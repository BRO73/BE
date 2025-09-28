package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.response.MenuItemResponse1;
import com.example.restaurant_management.entity.MenuItem;

import java.util.List;

public interface MenuItemService1 {
    List<MenuItemResponse1> getAllMenuItems();
}
