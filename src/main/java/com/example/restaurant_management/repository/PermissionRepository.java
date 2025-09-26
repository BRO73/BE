package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Set<Permission> findAllByIdIn(Set<Long> longs);
}
