package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.MenuItemRequest;
import com.example.restaurant_management.dto.response.MenuItemResponse;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.mapper.MenuItemMapper;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.service.MenuItemService;
import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
@AllArgsConstructor
@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemMapper menuItemMapper;
    private final MenuItemRepository menuItemRepository;

    @Override
    public List<MenuItemResponse> getAllMenuItems() {
        return menuItemMapper.toDTOList(menuItemRepository.findAll());
    }

    @Override
    public Optional<MenuItem> getMenuItemById(Long id) {
        return menuItemRepository.findById(id);
    }

    @Override
    public MenuItem createMenuItem(MenuItemRequest request) {
        MenuItem menuItem = menuItemMapper.toEntity(request);
        System.out.println(menuItem.toString());
        return menuItemRepository.save(menuItem);
    }

    @Override
    public MenuItem updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem menuItem = menuItemMapper.toEntity(request);
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

    @Override
    public Page<MenuItemResponse> getAllMenuItemsPaged(Pageable pageable) {
        return menuItemRepository.findAll(pageable)
                .map(menuItemMapper::toDTO);
    }
}
