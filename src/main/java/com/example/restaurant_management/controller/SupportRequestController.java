package com.example.restaurant_management.controller;

import com.example.restaurant_management.entity.SupportRequest;
import com.example.restaurant_management.service.SupportRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support-requests")
public class SupportRequestController {

    private final SupportRequestService supportRequestService;

    public SupportRequestController(SupportRequestService supportRequestService) {
        this.supportRequestService = supportRequestService;
    }

    @GetMapping
    public ResponseEntity<List<SupportRequest>> getAllSupportRequests() {
        return ResponseEntity.ok(supportRequestService.getAllSupportRequests());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupportRequest> getSupportRequestById(@PathVariable Long id) {
        return supportRequestService.getSupportRequestById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SupportRequest> createSupportRequest(@RequestBody SupportRequest supportRequest) {
        return ResponseEntity.ok(supportRequestService.createSupportRequest(supportRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupportRequest> updateSupportRequest(@PathVariable Long id, @RequestBody SupportRequest supportRequest) {
        return ResponseEntity.ok(supportRequestService.updateSupportRequest(id, supportRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupportRequest(@PathVariable Long id) {
        supportRequestService.deleteSupportRequest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<SupportRequest>> getSupportRequestsByTable(@PathVariable Long tableId) {
        return ResponseEntity.ok(supportRequestService.getSupportRequestsByTable(tableId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SupportRequest>> getSupportRequestsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(supportRequestService.getSupportRequestsByStatus(status));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<SupportRequest>> getSupportRequestsByType(@PathVariable String type) {
        return ResponseEntity.ok(supportRequestService.getSupportRequestsByType(type));
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<SupportRequest>> getSupportRequestsByStaff(@PathVariable Long staffId) {
        return ResponseEntity.ok(supportRequestService.getSupportRequestsByStaff(staffId));
    }
}
