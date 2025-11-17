package com.example.restaurant_management.repository;


import com.example.restaurant_management.entity.FloorElement;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface FloorElementRepository extends JpaRepository<FloorElement, String> {
    // Cách 1: dùng @EntityGraph để fetch table & location
    @EntityGraph(attributePaths = {"table", "location"})
    List<FloorElement> findAll();

    @EntityGraph(attributePaths = {"table", "location"})
    java.util.Optional<FloorElement> findById(String id);
}
