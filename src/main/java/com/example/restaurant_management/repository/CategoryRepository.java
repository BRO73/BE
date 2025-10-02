package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Ví dụ: thêm custom query nếu cần
    boolean existsByName(String name);
    Optional<Category> findByName(String name);

}
