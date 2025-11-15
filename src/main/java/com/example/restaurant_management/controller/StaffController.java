package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.StaffProfileResponse;
import com.example.restaurant_management.dto.response.RestaurantResponse;
import com.example.restaurant_management.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse<StaffProfileResponse>> getProfile(Authentication authentication) {
        return RestaurantResponse.ok(staffService.getProfile(authentication));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse<List<StaffResponse>>> getAllStaff() {
        return RestaurantResponse.ok(staffService.getAllStaff());
    }

    // 🟢 CREATE STAFF
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse<StaffResponse>> createStaff(
            @RequestBody CreateStaffRequest request
    ) {
        return RestaurantResponse.ok(staffService.createStaff(request), "Create staff successfully");
    }

    // 🟡 UPDATE STAFF
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse<StaffResponse>> updateStaff(
            @PathVariable Long id,
            @RequestBody UpdateStaffRequest request
    ) {
        return RestaurantResponse.ok(staffService.updateStaff(id, request), "Update staff successfully");
    }

    // 🔴 DELETE STAFF
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RestaurantResponse<Void>> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return RestaurantResponse.ok(null, "Delete staff successfully");
    }
}
