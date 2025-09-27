package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.service.MenuItemService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Override
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    @Override
    public MenuItem createMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItem menuItem) {
        menuItem.setId(id);
        return menuItemRepository.save(menuItem);
    }

    @Override
    public void deleteMenuItem(Long id) {
        menuItemRepository.deleteById(id);
    }

    @Override
    public List<MenuItem> getMenuItemsByCategory(Long categoryId) {
        return menuItemRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<MenuItem> getMenuItemsByStatus(String status) {
        return menuItemRepository.findByStatus(status);
    }

    @Override
    public List<MenuItem> searchMenuItemsByName(String name) {
        return menuItemRepository.findByNameContainingIgnoreCase(name);
    }
}
