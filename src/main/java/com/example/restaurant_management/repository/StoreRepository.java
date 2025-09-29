package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Store findByStoreName(String name);
}
