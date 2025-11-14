package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.CreateStaffAccountRequest;
import com.example.restaurant_management.dto.request.UpdateUserAccountRequest;
import com.example.restaurant_management.dto.response.StaffAccountResponse;

import java.util.List;

public interface StaffAccountService {
    List<StaffAccountResponse> list();
    StaffAccountResponse update(Long userId, UpdateUserAccountRequest req);
    void delete(Long userId);
    StaffAccountResponse create(CreateStaffAccountRequest req);
}
