package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.response.MenuItemResponse1;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.mapper.MenuItem1Mapper;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.repository.MenuItemRepository1;
import com.example.restaurant_management.service.MenuItemService1;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MenuItemService1Impl implements MenuItemService1 {

    private final MenuItemRepository1 menuItemRepository1;
    private final MenuItem1Mapper menuItem1Mapper;

    @Override
    public List<MenuItemResponse1> getAllMenuItems() {
        List<MenuItemResponse1> menuItemResponse1List = new ArrayList<>();

        List<MenuItem> menuItems = menuItemRepository1.findAll();

        for (MenuItem menuItem : menuItems) {
            MenuItemResponse1 menuItemResponse1 = menuItem1Mapper.toDTO(menuItem);
            menuItemResponse1List.add(menuItemResponse1);
        }

        return menuItemResponse1List;
    }
}