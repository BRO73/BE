package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.SupportRequest;

import java.util.List;
import java.util.Optional;

public interface SupportRequestService {
    List<SupportRequest> getAllSupportRequests();
    Optional<SupportRequest> getSupportRequestById(Long id);
    SupportRequest createSupportRequest(SupportRequest supportRequest);
    SupportRequest updateSupportRequest(Long id, SupportRequest supportRequest);
    void deleteSupportRequest(Long id);
    List<SupportRequest> getSupportRequestsByTable(Long tableId);
    List<SupportRequest> getSupportRequestsByStatus(String status);
    List<SupportRequest> getSupportRequestsByType(String type);
    List<SupportRequest> getSupportRequestsByStaff(Long staffId);
}
