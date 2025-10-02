package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.CategoryRequest;
import com.example.restaurant_management.dto.response.CategoryResponse;
import com.example.restaurant_management.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    List<CategoryResponse> getAllCategories();

    Optional<CategoryResponse> getCategoryById(Long id);

    // Chỉ thay input từ Category sang CategoryRequest
    CategoryResponse createCategory(CategoryRequest request);

    Optional<CategoryResponse> updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    Optional<CategoryResponse> getCategoryByName(String name);

    Category getCategoryEntityByName(String categoryName);
}
