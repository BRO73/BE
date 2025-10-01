package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.request.UserRequest;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.dto.response.UserResponse;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.mapper.UserMapper;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.StoreRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {

        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userList) {
            UserResponse userResponse = userMapper.toResponse(user);
            userResponseList.add(userResponse);
        }
        return userResponseList;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        User user = User.builder()
                .username(request.username())
                .hashedPassword(passwordEncoder.encode(request.password()))
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .userType(User.UserType.STAFF)
                .store(storeRepository.findByName(request.storeName()).orElseThrow(() -> new RestaurantException("Store not found")))
                .build();
        userRepository.save(user);
        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new RestaurantException("Role not found"));

        UserRole userRole = UserRole.builder()
                .userId(user.getId())
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);

        return userMapper.toResponse(user);
    }

    @Override
    public User updateStaffInfo(String username, UpdateStaffRequest request) {
        User user = getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEmail(request.email());
        user.setPhoneNumber(request.phoneNumber());

        if (request.password() != null && !request.password().isBlank()) {
            user.setHashedPassword(passwordEncoder.encode(request.password()));
        }

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        // Note: This method needs to be added to UserRepository
        return Optional.empty();
    }

    @Override
    public List<User> getUsersByEmail(String email) {
        // Note: This method needs to be added to UserRepository
        return List.of();
    }

    @Override
    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
