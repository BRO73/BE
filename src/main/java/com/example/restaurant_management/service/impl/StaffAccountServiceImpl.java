package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.CreateStaffAccountRequest;
import com.example.restaurant_management.dto.request.UpdateUserAccountRequest;
import com.example.restaurant_management.dto.response.StaffAccountResponse;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.StaffRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.StaffAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffAccountServiceImpl implements StaffAccountService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    // ================== LIST ==================

    @Override
    public List<StaffAccountResponse> list() {

        log.info("=== STAFF ACCOUNT LIST() START ===");

        List<Staff> allStaff = staffRepository.findAll();
        log.info("Total staff in DB = {}", allStaff.size());

        allStaff.forEach(s -> log.info(
                "Staff id={} fullName={} userId={} role={} passwordText={}",
                s.getId(),
                s.getFullName(),
                s.getUser() != null ? s.getUser().getId() : null,
                s.getRole(),
                s.getPasswordText()
        ));

        List<StaffAccountResponse> result = allStaff.stream()
                .filter(s -> s.getUser() != null)
                .map(s -> {
                    User u = s.getUser();
                    return new StaffAccountResponse(
                            u.getId(),
                            u.getUsername(),
                            s.getRole(),
                            s.getFullName(),
                            s.getPasswordText(),
                            u.getCreatedAt()
                    );
                })
                .toList();

        log.info("Total ACCOUNTS found = {}", result.size());
        log.info("=== STAFF ACCOUNT LIST() END ===");

        return result;
    }



    // ================== CREATE ==================

    @Override
    @Transactional
    public StaffAccountResponse create(CreateStaffAccountRequest req) {

        // 1. Tìm staff
        Staff staff = staffRepository.findById(req.staffId())
                .orElseThrow(() -> new RestaurantException(
                        ErrorEnum.STAFF_NOT_FOUND,
                        "Staff not found with id = " + req.staffId()
                ));

        // 2. Staff đã có user rồi thì không cho tạo nữa
        if (staff.getUser() != null) {
            throw new RestaurantException(
                    ErrorEnum.STAFF_ALREADY_HAS_USER,
                    ErrorEnum.STAFF_ALREADY_HAS_USER.getMessage()
            );
        }

        // 3. Check trùng username
        if (userRepository.existsByUsernameIgnoreCase(req.username())) {
            throw new RestaurantException(
                    ErrorEnum.USERNAME_TAKEN,
                    ErrorEnum.USERNAME_TAKEN.getMessage()
            );
        }

        // 4. Tạo User với password hash
        User user = new User();
        user.setUsername(req.username());
        user.setHashedPassword(passwordEncoder.encode(req.password()));
        user = userRepository.save(user);

        // 5. Gắn user vào staff + lưu plain password
        staff.setUser(user);
        staff.setPasswordText(req.password());
        staffRepository.save(staff);

        // 6. Gán role cho user thông qua bảng user_roles
        String roleCode = staff.getRole(); // ví dụ: "WAITER", "CHEF"...

        if (roleCode != null && !roleCode.isBlank()) {
            String normalized = roleCode.toUpperCase(Locale.ROOT);

            Role role = roleRepository.findByName(normalized)
                    .orElseThrow(() -> new RestaurantException(
                            ErrorEnum.ROLE_NOT_FOUND,
                            "Role not found: " + normalized));

            // đảm bảo mỗi user có 1 role
            userRoleRepository.deleteAllByUserId(user.getId());

            UserRole userRole = UserRole.builder()
                    .userId(user.getId())
                    .roleId(role.getId())
                    .build();

            userRoleRepository.save(userRole);
        }

        return new StaffAccountResponse(
                user.getId(),
                user.getUsername(),
                staff.getRole(),
                staff.getFullName(),
                staff.getPasswordText(),
                user.getCreatedAt()
        );
    }

    // ================== UPDATE ==================

    @Override
    @Transactional
    public StaffAccountResponse update(Long userId, UpdateUserAccountRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestaurantException(
                        ErrorEnum.USER_NOT_FOUND,
                        "User not found with id = " + userId
                ));

        // username
        user.setUsername(req.username());

        // password
        if (req.password() != null && !req.password().isBlank()) {
            user.setHashedPassword(passwordEncoder.encode(req.password()));

            Staff staff = user.getStaff();
            if (staff != null) {
                staff.setPasswordText(req.password());
                staffRepository.save(staff);
            }
        }

        Staff staff = user.getStaff();

        // role (map vào Staff.role + bảng user_roles)
        if (req.role() != null && staff != null) {

            staff.setRole(req.role());
            staffRepository.save(staff);

            String normalized = req.role().toUpperCase(Locale.ROOT);

            Role role = roleRepository.findByName(normalized)
                    .orElseThrow(() -> new RestaurantException(
                            ErrorEnum.ROLE_NOT_FOUND,
                            "Role not found: " + normalized));

            userRoleRepository.deleteAllByUserId(user.getId());

            UserRole userRole = UserRole.builder()
                    .userId(user.getId())
                    .roleId(role.getId())
                    .build();

            userRoleRepository.save(userRole);
        }

        userRepository.save(user);

        Staff s = user.getStaff();
        return new StaffAccountResponse(
                user.getId(),
                user.getUsername(),
                s != null ? s.getRole() : null,
                s != null ? s.getFullName() : null,
                s != null ? s.getPasswordText() : null,
                user.getCreatedAt()
        );
    }

    // ================== DELETE ==================

    @Override
    @Transactional
    public void delete(Long userId) {
        if (userId == null) {
            throw new RestaurantException(
                    ErrorEnum.INVALID_INPUT_COMMON,
                    "User id is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestaurantException(
                        ErrorEnum.USER_NOT_FOUND,
                        "User not found with id = " + userId));

        // 1) Nếu có Staff linked, detach trước để tránh lỗi FK
        Staff staff = user.getStaff();
        if (staff != null) {
            staff.setUser(null);
            staff.setPasswordText(null);
            staffRepository.save(staff);
        }

        // 2) Xoá tất cả user_role liên quan
        userRoleRepository.deleteAllByUserId(user.getId());

        // 3) Xoá user
        userRepository.delete(user);
    }
}
