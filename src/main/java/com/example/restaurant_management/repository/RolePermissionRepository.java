package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Set<RolePermission> findAllByRoleIdIn(Set<Long> id);
}
