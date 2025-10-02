package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface StaffService {
    User updateStaffInfo(String username, UpdateStaffRequest request);
    UserProfileResponse getProfile(Authentication authentication);

    StaffResponse createStaff(CreateStaffRequest createStaffRequest, Authentication authentication);

    List<StaffResponse> getAllStaffInStore(Authentication authentication);

    StaffResponse updateStaff(Long staffId, UpdateStaffRequest updateStaffRequest, Authentication authentication);
}
