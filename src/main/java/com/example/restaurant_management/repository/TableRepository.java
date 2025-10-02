package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TableRepository extends JpaRepository<TableEntity, Long> {
    Optional<TableEntity> findByTableNumber(String tableNumber);
    List<TableEntity> findByStatus(String status);
    List<TableEntity> findByCapacityGreaterThanEqual(Integer capacity);
    List<TableEntity> findByLocation(Location location);
}
