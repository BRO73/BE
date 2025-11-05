package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.MenuItemRequest;
import com.example.restaurant_management.dto.response.MenuItemResponse;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.mapper.MenuItemMapper;
import com.example.restaurant_management.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
@AllArgsConstructor
@RestController
@RequestMapping("/api/menu-items")
public class MenuItemController {
    private final MenuItemService menuItemService;
    private final MenuItemMapper menuItemMapper;

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        return ResponseEntity.ok(menuItemService.getAllMenuItems());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<MenuItemResponse>> getAllMenuItemsPaged(
            @PageableDefault(size = 10, page = 0) Pageable pageable) {
        return ResponseEntity.ok(menuItemService.getAllMenuItemsPaged(pageable));
    }
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemResponse> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItem> opt = menuItemService.getMenuItemById(id);
        System.out.println("Fetched from DB: " + opt);
        return opt
                .map(menuItemMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(@RequestBody MenuItemRequest request) {
        MenuItem menuItem = menuItemService.createMenuItem(request); // tạo entity
        MenuItemResponse response = menuItemMapper.toDTO(menuItem);

        return ResponseEntity.ok(response); // trả về DTO
    }


    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(
            @PathVariable Long id,
            @RequestBody MenuItemRequest request) {

        MenuItem updatedMenuItem = menuItemService.updateMenuItem(id, request); // update entity
        MenuItemResponse response = menuItemMapper.toDTO(updatedMenuItem); // map sang DTO
        return ResponseEntity.ok(response); // trả về DTO giống POST
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByCategory(categoryId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<MenuItem>> getMenuItemsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(menuItemService.getMenuItemsByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchMenuItemsByName(@RequestParam String name) {
        return ResponseEntity.ok(menuItemService.searchMenuItemsByName(name));
    }
}
