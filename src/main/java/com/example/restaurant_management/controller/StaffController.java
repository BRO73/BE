package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.response.RestaurantResponse;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staffs")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping("/my-store-staff")
    public ResponseEntity<List<StaffResponse>> getAllStaffOfStore(
            Authentication authentication) {
        return ResponseEntity.ok(staffService.getAllStaffInStore(authentication));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<StaffResponse> createStaff(
            @Valid @RequestBody CreateStaffRequest createStaffRequest,
            Authentication authentication) {
        return ResponseEntity.ok(staffService.createStaff(createStaffRequest, authentication));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{staffId}")
    public ResponseEntity<StaffResponse> updateStaff(
            @Valid @PathVariable Long staffId,
            @RequestBody UpdateStaffRequest updateStaffRequest,
            Authentication authentication) {
        return ResponseEntity.ok(
                staffService.updateStaff(staffId, updateStaffRequest, authentication)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> deleteStaff(
            @PathVariable Long staffId,
            Authentication authentication
    ) {
        staffService.deleteStaff(staffId, authentication);
        return ResponseEntity.noContent().build(); // 204
    }


//    @GetMapping("/profile")
//    public ResponseEntity<RestaurantResponse<UserProfileResponse>> getProfile(
//            Authentication authentication
//    ) {
//        UserProfileResponse profile = staffService.getProfile(authentication);
//        return RestaurantResponse.ok(profile, "Staff profile fetched successfully");
//    }
//
//    @PostMapping("/profile")
//    public ResponseEntity<RestaurantResponse<User>> updateStaff(
//            @AuthenticationPrincipal String username,
//            @RequestBody UpdateStaffRequest request
//    ) {
//        User updatedUser = staffService.updateStaffInfo(username, request);
//        return RestaurantResponse.ok(updatedUser, "Staff information updated successfully");
//    }
}