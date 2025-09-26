package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Set<UserRole> findAllByUserId(Long id);
}
