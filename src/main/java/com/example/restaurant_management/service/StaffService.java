package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;
import org.springframework.security.core.Authentication;

public interface StaffService {
    User updateStaffInfo(String username, UpdateStaffRequest request);
    UserProfileResponse getProfile(Authentication authentication);
}
