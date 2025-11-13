package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.enums.StaffRole;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.StaffRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    private static final Map<StaffRole, String> ROLE_DB_MAP = Map.of(
            StaffRole.WAITER, "WAITSTAFF",
            StaffRole.CHEF, "KITCHEN_STAFF",
            StaffRole.CASHIER, "CASHIER",
            StaffRole.ADMIN, "ADMIN"
    );

    /* ===================== API chính ===================== */

    @Override
    public List<StaffResponse> getAllStaffInStore(Authentication authentication) {
        return staffRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public StaffResponse createStaff(CreateStaffRequest req, Authentication authentication) {
        if (req == null) {
            throw new RestaurantException(ErrorEnum.INVALID_INPUT, "Invalid request");
        }
        if (isBlank(req.fullName())) {
            throw new RestaurantException(ErrorEnum.INVALID_INPUT_COMMON, "Full name is required.");
        }
        if (isBlank(req.email()) || !req.email().contains("@")) {
            throw new RestaurantException(ErrorEnum.INVALID_INPUT, "Invalid email format");
        }

        // email unique
        if (staffRepository.existsByEmail(req.email())) {
            throw new RestaurantException(ErrorEnum.STAFF_EMAIL_EXIST,
                    ErrorEnum.STAFF_EMAIL_EXIST.getMessage());
        }

        // phone unique
        if (staffRepository.existsByPhoneNumber(req.phoneNumber())) {
            throw new RestaurantException(ErrorEnum.STAFF_PHONE_EXIST,
                    ErrorEnum.STAFF_PHONE_EXIST.getMessage());
        }

        String normalizedRole = normalizeRoleForDB(req.role().name());

        // Tạo Staff KHÔNG gắn User, KHÔNG set passwordText
        Staff staff = Staff.builder()
                .fullName(req.fullName())
                .email(req.email())
                .phoneNumber(req.phoneNumber())
                .role(normalizedRole)
                .storeId(req.storeId())
                .build();

        staff = staffRepository.save(staff);

        log.info("STAFF-CREATE: id={}, fullName={}, role={}",
                staff.getId(), staff.getFullName(), staff.getRole());

        return toResponse(staff);
    }

    @Override
    @Transactional
    public StaffResponse updateStaff(Long staffId,
                                     UpdateStaffRequest req,
                                     Authentication authentication) {

        // 1. Tìm staff cần update
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RestaurantException(
                        ErrorEnum.STAFF_NOT_FOUND,
                        "Staff not found with id = " + staffId
                ));

        // 2. Validate email không trùng với staff khác
        if (!staff.getEmail().equalsIgnoreCase(req.email())) {
            staffRepository.findByEmail(req.email())
                    .filter(other -> !other.getId().equals(staffId))
                    .ifPresent(other -> {
                        throw new RestaurantException(
                                ErrorEnum.STAFF_EMAIL_EXIST,
                                ErrorEnum.STAFF_EMAIL_EXIST.getMessage()
                        );
                    });
        }

        // 3. Validate phone không trùng với staff khác
        if (!staff.getPhoneNumber().equalsIgnoreCase(req.phoneNumber())) {
            staffRepository.findByPhoneNumber(req.phoneNumber())
                    .filter(other -> !other.getId().equals(staffId))
                    .ifPresent(other -> {
                        throw new RestaurantException(
                                ErrorEnum.STAFF_PHONE_EXIST,
                                ErrorEnum.STAFF_PHONE_EXIST.getMessage()
                        );
                    });
        }

        // 4. Cập nhật thông tin cơ bản
        staff.setFullName(req.fullName());
        staff.setEmail(req.email());
        staff.setPhoneNumber(req.phoneNumber());

        // 5. Cập nhật trạng thái kích hoạt nếu có dùng
        if (req.isActivated() != null) {
            staff.setActivated(req.isActivated());
        }

        // 6. Cập nhật role nghiệp vụ nếu được gửi lên
        if (req.role() != null) {
            String normalizedRole = normalizeRoleForDB(req.role().name());
            staff.setRole(normalizedRole);
        }

        Staff saved = staffRepository.save(staff);
        return toResponse(saved);
    }

    @Override
    public UserProfileResponse getProfile(Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        Long userId = credentialPayload.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestaurantException(ErrorEnum.USER_NOT_FOUND,
                        ErrorEnum.USER_NOT_FOUND.getMessage()));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Override
    public User updateStaffInfo(String username, UpdateStaffRequest request) {
        if (isBlank(username)) {
            throw new RestaurantException(ErrorEnum.INVALID_INPUT_COMMON, "Username is required");
        }
        User user = userRepository.findByUsername(username.trim())
                .orElseThrow(() -> new RestaurantException(ErrorEnum.USER_NOT_FOUND,
                        ErrorEnum.USER_NOT_FOUND.getMessage()));

        // hiện tại không đổi gì, chỉ return lại; sau này nếu muốn map field thì thêm
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteStaff(Long staffId, Authentication authentication) {
        if (staffId == null) {
            throw new RestaurantException(ErrorEnum.INVALID_INPUT_COMMON, "Staff id is required");
        }

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RestaurantException(ErrorEnum.STAFF_NOT_FOUND,
                        ErrorEnum.STAFF_NOT_FOUND.getMessage()));

        User linkedUser = staff.getUser();
        if (linkedUser != null) {
            userRoleRepository.deleteAllByUserId(linkedUser.getId());
            staff.setUser(null);
            staffRepository.save(staff);
            userRepository.delete(linkedUser);
        }

        staffRepository.delete(staff);
        log.info("STAFF-DELETE: id={} deleted successfully", staffId);
    }

    /* ===================== Helpers ===================== */

    private StaffResponse toResponse(Staff s) {
        return StaffResponse.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .email(s.getEmail())
                .phoneNumber(s.getPhoneNumber())
                .isActivated(s.isActivated())
                .role(s.getRole())
                .userId(s.getUser() != null ? s.getUser().getId() : null)
                .createdAt(s.getCreatedAt())
                .build();
    }

    private String normalizeRoleForDB(Object roleObj) {
        if (roleObj == null) return ROLE_DB_MAP.get(StaffRole.WAITER);

        if (roleObj instanceof String roleStr) {
            try {
                StaffRole enumVal = StaffRole.valueOf(roleStr.trim().toUpperCase(Locale.ROOT));
                return ROLE_DB_MAP.getOrDefault(enumVal, enumVal.name());
            } catch (IllegalArgumentException ex) {
                throw new RestaurantException(ErrorEnum.INVALID_INPUT_COMMON,
                        "Invalid role: " + roleStr);
            }
        }

        if (roleObj instanceof StaffRole sr) {
            return ROLE_DB_MAP.getOrDefault(sr, sr.name());
        }

        throw new RestaurantException(ErrorEnum.INVALID_INPUT_COMMON,
                "Unsupported role type: " + roleObj);
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    // upsertSingleUserRole vẫn còn nhưng hiện tại không dùng nữa trong flow mới
    private void upsertSingleUserRole(Long userId, String roleNameUpper) {
        if (userId == null) {
            throw new RestaurantException(ErrorEnum.USER_NOT_FOUND, "User not linked to staff");
        }

        Role role = roleRepository.findByName(roleNameUpper)
                .orElseThrow(() -> new RestaurantException(ErrorEnum.ROLE_NOT_FOUND,
                        "Role not found: " + roleNameUpper));

        userRoleRepository.deleteAllByUserId(userId);

        userRoleRepository.save(UserRole.builder()
                .userId(userId)
                .roleId(role.getId())
                .build());

        log.info("USER_ROLES UPSERT: userId={} -> role={}", userId, roleNameUpper);
    }
}
