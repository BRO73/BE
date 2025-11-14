package com.example.restaurant_management.mapper;

import com.example.restaurant_management.dto.request.MenuItemRequest;
import com.example.restaurant_management.dto.response.CategoryResponse;
import com.example.restaurant_management.dto.response.MenuItemResponse;
import com.example.restaurant_management.entity.Category;
import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.service.CategoryService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class MenuItemMapper {

    @Autowired
    protected CategoryService categoryService;

    @Mapping(target = "category", expression = "java(toCategory(menuItemRequest.getCategoryName()))")
    public abstract MenuItem toEntity(MenuItemRequest menuItemRequest);

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    public abstract MenuItemResponse toDTO(MenuItem menuItem);

    public List<MenuItemResponse> toDTOList(List<MenuItem> menuItems) {
        return menuItems.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // helper method: lấy Category thực tế từ DB
    protected Category toCategory(String categoryName) {
        if (categoryName == null) return null;
        return categoryService.getCategoryEntityByName(categoryName);
    }
}
