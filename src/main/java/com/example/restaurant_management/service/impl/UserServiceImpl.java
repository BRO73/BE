package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateStaffInfo(String username, UpdateStaffRequest request) {
        User user = getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.fullName());
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
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }
}
