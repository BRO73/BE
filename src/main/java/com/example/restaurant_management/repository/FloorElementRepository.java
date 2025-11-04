package com.example.restaurant_management.repository;


import com.example.restaurant_management.entity.FloorElement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FloorElementRepository extends JpaRepository<FloorElement, String> {}