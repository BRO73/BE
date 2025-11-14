package com.example.restaurant_management.repository;

import com.example.restaurant_management.common.enums.StaffRole;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUser(User user);
    // Liên kết staff ↔ user
    Optional<Staff> findByUserId(Long userId);

    // Tra cứu/unique helpers
    Optional<Staff> findByEmail(String email);
    Optional<Staff> findByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    // Thống kê theo role (nếu cần)
    long countByRole(StaffRole role);
}
