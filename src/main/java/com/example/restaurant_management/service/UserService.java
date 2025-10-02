package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.CreateUserRequest;
import com.example.restaurant_management.dto.request.UpdateUserRequest;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.dto.response.UserResponse;
import com.example.restaurant_management.entity.User;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<UserResponse> getAllUsers();
    Optional<User> getUserById(Long id);
    UserResponse createUser(CreateUserRequest createUserRequest, Authentication authentication);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByPhoneNumber(String phoneNumber);
    List<User> getUsersByEmail(String email);
    UserProfileResponse getProfile(String username);
    List<UserResponse> getAllUserInStore(Authentication authentication);
}
