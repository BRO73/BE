package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.response.MenuItemResponse1;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.service.MenuItemService1;
import com.example.restaurant_management.service.impl.MenuItemService1Impl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items-1")
public class MenuItemController1 {

    private final MenuItemService1 menuItemService1;

    public MenuItemController1(MenuItemService1 menuItemService1) {
        this.menuItemService1 = menuItemService1;
    }

    @GetMapping()
    public ResponseEntity<List<MenuItemResponse1>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService1.getAllMenuItems());
    }

}
