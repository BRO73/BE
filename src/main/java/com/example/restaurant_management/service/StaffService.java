package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.request.UpdateProfileRequest;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.StaffProfileResponse;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface StaffService {
    StaffProfileResponse getProfile(Authentication authentication);
    User updateStaffInfo(String username, UpdateStaffRequest request);
    StaffProfileResponse updateProfile(String username, UpdateProfileRequest request);
    List<StaffResponse> getAllStaff();

    // CRUD mới
    StaffResponse createStaff(CreateStaffRequest request);
    StaffResponse updateStaff(Long id, UpdateStaffRequest request);
    void deleteStaff(Long id);
}

