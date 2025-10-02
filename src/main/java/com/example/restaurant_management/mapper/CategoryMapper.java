package com.example.restaurant_management.mapper;

import com.example.restaurant_management.dto.request.CategoryRequest;
import com.example.restaurant_management.dto.response.CategoryResponse;
import com.example.restaurant_management.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class CategoryMapper {

    // Mapping DTO -> Entity
    public Category toEntity(CategoryRequest request) {
        if (request == null) return null;

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        return category;
    }

    // Mapping Entity -> DTO
    public CategoryResponse toDTO(Category entity) {
        if (entity == null) return null;

        CategoryResponse response = new CategoryResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setImageUrl(entity.getImageUrl());
        return response;
    }

    // Mapping List<Entity> -> List<DTO>
    public List<CategoryResponse> toDTOList(List<Category> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
