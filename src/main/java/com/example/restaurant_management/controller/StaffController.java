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

    // 🟢 Lấy thông tin profile staff hiện tại
    @GetMapping("/profile")
    @PreAuthorize("hasRole('WAITSTAFF')")
    public ResponseEntity<RestaurantResponse<StaffProfileResponse>> getProfile(Authentication authentication) {
        return RestaurantResponse.ok(staffService.getProfile(authentication));
    }

}