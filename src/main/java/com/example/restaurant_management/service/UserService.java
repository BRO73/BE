package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);
    User createUser(User user);
    User updateStaffInfo(String username, UpdateStaffRequest request);
    void deleteUser(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByPhoneNumber(String phoneNumber);
    List<User> getUsersByEmail(String email);
    UserProfileResponse getProfile(String username);

}
