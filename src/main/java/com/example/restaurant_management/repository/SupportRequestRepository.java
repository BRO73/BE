package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.SupportRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRequestRepository extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findByTableId(Long tableId);
    List<SupportRequest> findByStatus(String status);
    List<SupportRequest> findByRequestType(String requestType);
    List<SupportRequest> findByStaffId(Long staffId);
}
