package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;

public interface StaffService {
    User updateStaffInfo(String username, UpdateStaffRequest request);
    UserProfileResponse getProfile(String username);
}
