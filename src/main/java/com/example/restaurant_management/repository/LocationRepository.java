package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    Optional<Location> findByName(String name);
    boolean existsByName(String name);
}
