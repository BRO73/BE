package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findAllByIdIn(Set<Long> longs);
}
