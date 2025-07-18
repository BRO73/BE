package com.example.demo_innocode.repository;

import com.example.demo_innocode.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Thêm custom query nếu cần
}
