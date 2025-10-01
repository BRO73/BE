package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Table;
import com.example.restaurant_management.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<Table, Long> {
    Optional<Table> findByTableNumber(String tableNumber);
    List<Table> findByStatus(String status);
    List<Table> findByCapacityGreaterThanEqual(Integer capacity);
    List<Table> findByLocation(Location location);
}
