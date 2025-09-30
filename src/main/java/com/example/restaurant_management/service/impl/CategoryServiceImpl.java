package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.CategoryRequest;
import com.example.restaurant_management.dto.response.CategoryResponse;
import com.example.restaurant_management.entity.Category;
import com.example.restaurant_management.mapper.CategoryMapper;
import com.example.restaurant_management.repository.CategoryRepository;
import com.example.restaurant_management.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categoryMapper.toDTOList(categories);
    }

    @Override
    public Optional<CategoryResponse> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO);
    }

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Category name already exists");
        }
        Category entity = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(entity);
        return categoryMapper.toDTO(saved);
    }

    @Override
    public Optional<CategoryResponse> updateCategory(Long id, CategoryRequest request) {
        return categoryRepository.findById(id)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setDescription(request.getDescription());
                    existing.setImageUrl(request.getImageUrl());
                    Category updated = categoryRepository.save(existing);
                    return categoryMapper.toDTO(updated);
                });
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Optional<CategoryResponse> getCategoryByName(String name) {
        return categoryRepository.findByName(name)
                .map(categoryMapper::toDTO);
    }

    // Method dùng cho MenuItemMapper để lấy entity
    public Category getCategoryEntityByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category not found: " + name));
    }
}
