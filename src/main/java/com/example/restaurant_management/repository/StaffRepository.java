package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {

    Optional<Staff> findByUser(User user);
}
