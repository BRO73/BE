//package com.example.restaurant_management.service.impl;
//
//import com.example.restaurant_management.dto.request.UpdateStaffRequest;
//import com.example.restaurant_management.dto.response.UserProfileResponse;
//import com.example.restaurant_management.entity.User;
//import com.example.restaurant_management.repository.UserRepository;
//import com.example.restaurant_management.service.StaffService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class StaffServiceImpl implements StaffService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    public User updateStaffInfo(String username, UpdateStaffRequest request) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        user.setFullName(request.fullName());
//        user.setEmail(request.email());
//        user.setPhoneNumber(request.phoneNumber());
//
//        if (request.password() != null && !request.password().isBlank()) {
//            user.setHashedPassword(passwordEncoder.encode(request.password()));
//        }
//
//        return userRepository.save(user);
//    }
//
//    @Override
//    public UserProfileResponse getProfile(Authentication authentication) {
//        Long userId = (Long
//        User user = userRepository.findById()
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        return UserProfileResponse.builder()
//                .id(user.getId())
//                .username(user.getUsername())
//                .fullName(user.getFullName())
//                .email(user.getEmail())
//                .phoneNumber(user.getPhoneNumber())
//                .build();
//    }
//}
