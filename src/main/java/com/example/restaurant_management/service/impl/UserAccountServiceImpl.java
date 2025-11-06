package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.enums.StaffRole;
import com.example.restaurant_management.dto.request.UpdateUserAccountRequest;
import com.example.restaurant_management.dto.response.UserAccountResponse;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.repository.StaffRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserAccountResponse> list() {
        // load tất cả user có staff
        return userRepository.findAll().stream()
                .filter(u -> u.getStaff() != null)
                .map(u -> {
                    Staff s = u.getStaff();
                    return new UserAccountResponse(
                            u.getId(),
                            u.getUsername(),
                            s.getRole(),
                            s.getFullName(),
                            s.getPasswordText(),
                            u.getCreatedAt()
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public UserAccountResponse update(Long userId, UpdateUserAccountRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // username
        user.setUsername(req.username());

        // password
        if (req.password() != null) {
            user.setHashedPassword(passwordEncoder.encode(req.password()));
            if (user.getStaff() != null) {
                user.getStaff().setPasswordText(req.password());
            }
        }

        // role (map vào Staff.role)
        if (req.role() != null && user.getStaff() != null) {
            String v = req.role().trim().toUpperCase();
            try {
                user.getStaff().setRole(StaffRole.valueOf(v));
            } catch (IllegalArgumentException ignored) {
                // bỏ qua nếu giá trị không hợp lệ
            }
        }

        userRepository.save(user);
        Staff s = user.getStaff();
        return new UserAccountResponse(
                user.getId(),
                user.getUsername(),
                s != null ? s.getRole() : null,
                s != null ? s.getFullName() : null,
                s != null ? s.getPasswordText() : null,
                user.getCreatedAt()
        );
    }
}
