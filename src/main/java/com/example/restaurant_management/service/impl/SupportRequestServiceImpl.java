package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.SupportRequest;
import com.example.restaurant_management.repository.SupportRequestRepository;
import com.example.restaurant_management.service.SupportRequestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupportRequestServiceImpl implements SupportRequestService {

    private final SupportRequestRepository supportRequestRepository;

    public SupportRequestServiceImpl(SupportRequestRepository supportRequestRepository) {
        this.supportRequestRepository = supportRequestRepository;
    }

    @Override
    public List<SupportRequest> getAllSupportRequests() {
        return supportRequestRepository.findAll();
    }

    @Override
    public Optional<SupportRequest> getSupportRequestById(Long id) {
        return supportRequestRepository.findById(id);
    }

    @Override
    public SupportRequest createSupportRequest(SupportRequest supportRequest) {
        return supportRequestRepository.save(supportRequest);
    }

    @Override
    public SupportRequest updateSupportRequest(Long id, SupportRequest supportRequest) {
        supportRequest.setId(id);
        return supportRequestRepository.save(supportRequest);
    }

    @Override
    public void deleteSupportRequest(Long id) {
        supportRequestRepository.deleteById(id);
    }

    @Override
    public List<SupportRequest> getSupportRequestsByTable(Long tableId) {
        return supportRequestRepository.findByTableId(tableId);
    }

    @Override
    public List<SupportRequest> getSupportRequestsByStatus(String status) {
        return supportRequestRepository.findByStatus(status);
    }

    @Override
    public List<SupportRequest> getSupportRequestsByType(String type) {
        return supportRequestRepository.findByRequestType(type);
    }

    @Override
    public List<SupportRequest> getSupportRequestsByStaff(Long staffId) {
        return supportRequestRepository.findByStaffId(staffId);
    }
}
