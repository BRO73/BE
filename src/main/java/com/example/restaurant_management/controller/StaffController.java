package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.RestaurantResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @PostMapping("/update")
    public ResponseEntity<RestaurantResponse<User>> updateStaff(
            @AuthenticationPrincipal String username,
            @RequestBody UpdateStaffRequest request
    ) {
        User updatedUser = staffService.updateStaffInfo(username, request);
        return RestaurantResponse.ok(updatedUser, "Staff information updated successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<RestaurantResponse<UserProfileResponse>> getProfile(
            Authentication authentication
    ) {
        UserProfileResponse profile = staffService.getProfile(authentication);
        return RestaurantResponse.ok(profile, "Staff profile fetched successfully");
    }
}
