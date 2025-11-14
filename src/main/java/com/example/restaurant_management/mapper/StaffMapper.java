package com.example.restaurant_management.mapper;

import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.StaffProfileResponse;
import com.example.restaurant_management.entity.Staff;
import org.springframework.stereotype.Component;

@Component
public class StaffMapper {

    public StaffResponse toResponse(Staff staff) {
        return StaffResponse.builder()
                .id(staff.getId())
                .name(staff.getFullName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .isActivated(true)
                .createdAt(staff.getCreatedAt())
                .userId(staff.getUser().getId())
                .build();
    }

    public StaffProfileResponse toProfileResponse(Long userId, String username,
                                                  String fullName, String email,
                                                  String phoneNumber, java.util.Set<String> roles) {
        return StaffProfileResponse.builder()
                .id(userId)
                .username(username)
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .roles(roles)
                .build();
    }
}