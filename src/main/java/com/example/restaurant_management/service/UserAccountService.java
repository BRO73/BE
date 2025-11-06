package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.UpdateUserAccountRequest;
import com.example.restaurant_management.dto.response.UserAccountResponse;

import java.util.List;

public interface UserAccountService {
    List<UserAccountResponse> list();
    UserAccountResponse update(Long userId, UpdateUserAccountRequest req);
}
