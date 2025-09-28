package com.example.restaurant_management.mapper;

import com.example.restaurant_management.dto.response.MenuItemResponse1;
import com.example.restaurant_management.entity.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItem1Mapper {

    @Mapping(source = "category.id", target = "categoryId")
    MenuItemResponse1 toDTO(MenuItem entity);

    @Mapping(source = "categoryId", target = "category.id")
    MenuItem toEntity(MenuItemResponse1 dto);
}

