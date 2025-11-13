package com.example.restaurant_management.mapper;

import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.StaffProfileResponse;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class StaffMapper {

    /**
     * Map Staff -> StaffResponse
     *  - Chỉ 1 role duy nhất (enum thuần: CHEF/WAITER/...)
     *  - Null-safe cho user
     *  - Lấy isActivated, createdAt nếu entity có
     */
    public StaffResponse toResponse(Staff staff) {
        if (staff == null) return null;
        String normalizedRole = normalizeEnumRole(staff.getRole());
        return StaffResponse.builder()
                .id(staff.getId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .isActivated(true) // hoặc map từ entity nếu bạn có field này
                .createdAt(staff.getCreatedAt())
                .userId(staff.getUser() != null ? staff.getUser().getId() : null)
                .role(normalizedRole) // ✅ luôn CHEF/WAITER/...
                .build();
    }

    /**
     * Dùng cho API /profile
     */
    public StaffProfileResponse toProfileResponse(Long userId, String username,
                                                  String fullName, String email,
                                                  String phoneNumber, Set<String> roles) {
        return StaffProfileResponse.builder()
                .id(userId)
                .username(username)
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .roles(roles)
                .build();
    }

    /* ========== Helpers ========== */

    private String normalizeEnumRole(String role) {
        if (role == null) return "WAITER";
        String r = role.trim();
        if (r.isEmpty()) return "WAITER";
        r = r.toUpperCase();
        if (r.startsWith("ROLE_")) r = r.substring(5); // strip prefix nếu có
        return r; // CHEF | WAITER | ...
    }

    private boolean getIsActivatedSafely(Staff s) {
        try {
            // Nếu entity Staff có field isActivated (Boolean)
            Boolean value = (Boolean) Staff.class.getMethod("getIsActivated").invoke(s);
            return value != null ? value : true;
        } catch (Exception e) {
            // Nếu entity không có, giữ nguyên true như mặc định
            return true;
        }
    }

    private LocalDateTime getCreatedAtSafely(Staff s) {
        try {
            Object val = Staff.class.getMethod("getCreatedAt").invoke(s);
            if (val instanceof LocalDateTime time) return time;
        } catch (Exception ignored) {}
        return null;
    }


}
