package com.example.restaurant_management.dto.response;

import java.time.LocalDateTime;

public record StaffAccountResponse(
        Long id,                // userId
        String username,
        String role,            // role của Staff
        String staffName,
        String passwordText,    // mật khẩu plain text nằm trong Staff
        LocalDateTime createdAt // từ User.createdAt
) {

}
