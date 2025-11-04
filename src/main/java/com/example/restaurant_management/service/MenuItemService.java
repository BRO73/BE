package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.MenuItemRequest;
import com.example.restaurant_management.dto.response.MenuItemResponse;
import com.example.restaurant_management.entity.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface MenuItemService {
    List<MenuItemResponse> getAllMenuItems();
    Optional<MenuItem> getMenuItemById(Long id);
    MenuItem createMenuItem(MenuItemRequest menuItem);
    MenuItem updateMenuItem(Long id, MenuItemRequest request);
    void deleteMenuItem(Long id);
    List<MenuItem> getMenuItemsByCategory(Long categoryId);
    List<MenuItem> getMenuItemsByStatus(String status);
    List<MenuItem> searchMenuItemsByName(String name);
<<<<<<< HEAD

=======
>>>>>>> 9c63813bdcf9ae648609546908fa8614fab988c4
    Page<MenuItemResponse> getAllMenuItemsPaged(Pageable pageable);
}
