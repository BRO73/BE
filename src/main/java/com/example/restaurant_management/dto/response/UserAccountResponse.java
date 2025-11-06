package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.common.enums.StaffRole;
import com.example.restaurant_management.entity.UserAccount;
import java.time.LocalDateTime;

public record UserAccountResponse(
        Long id,                // userId
        String username,
        StaffRole role,         // role của Staff
        String staffName,
        String passwordText,    // để FE prefill modal Edit
        LocalDateTime createdAt // từ User.createdAt
) {
    public static UserAccountResponse from(UserAccount ua) {
        return new UserAccountResponse(
                ua.getId(),
                ua.getUsername(),
                ua.getRole(),
                ua.getStaff() != null ? ua.getStaff().getFullName() : null,
                ua.getStaff() != null ? ua.getStaff().getPasswordText() : null,
                ua.getCreatedAt()
        );
    }
}
