package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    List<Category> getAllCategories();
    Optional<Category> getCategoryById(Long id);
    Category createCategory(Category category);
    Optional<Category> updateCategory(Long id, Category category);
    void deleteCategory(Long id);
}
