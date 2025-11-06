package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.enums.StaffRole;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.StaffRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;

    /** Lấy toàn bộ staff (không còn lọc theo store) */
    @Override
    public List<StaffResponse> getAllStaffInStore(Authentication authentication) {
        return staffRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /** Tạo staff mới — nếu muốn mặc định role, set ở đây */
    @Override
    @Transactional
    public StaffResponse createStaff(CreateStaffRequest req, Authentication authentication) {
        try {
            System.out.println("=== CREATE STAFF DEBUG ===");
            System.out.println("Request role: " + req.role());
            System.out.println("Authenticated user: " + authentication.getName());
            System.out.println("User authorities: " + authentication.getAuthorities());

            // 1) Tạo username từ email
            String username = extractUsernameFromEmail(req.email());
            String finalUsername = generateUniqueUsername(username);
            System.out.println("Generated username: " + finalUsername);

            // 2) Tạo User
            User user = User.builder()
                    .username(finalUsername)
                    .hashedPassword(passwordEncoder.encode(req.initialPassword()))
                    .build();
            user = userRepository.save(user);
            System.out.println("User created: " + user.getId());

            // 3) Tạo Staff
            Staff staff = Staff.builder()
                    .fullName(req.fullName())
                    .email(req.email())
                    .phoneNumber(req.phoneNumber())
                    .role(req.role() != null ? req.role() : StaffRole.WAITER)
                    .storeId(req.storeId())
                    .user(user)
                    .passwordText(req.initialPassword())
                    .build();

            staff = staffRepository.save(staff);
            System.out.println("Staff created with role: " + staff.getRole());

            return toResponse(staff);

        } catch (Exception e) {
            System.out.println("ERROR in createStaff: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /** Cập nhật staff — cho phép đổi role, liên kết user theo username nếu được cung cấp */
    @Override
    public StaffResponse updateStaff(Long staffId, UpdateStaffRequest req, Authentication authentication) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        if (req.fullName() != null) staff.setFullName(req.fullName());
        if (req.email() != null) staff.setEmail(req.email());
        if (req.phoneNumber() != null) staff.setPhoneNumber(req.phoneNumber());

        if (req.role() != null) {
            staff.setRole(req.role()); // WAITER / MANAGER / CHEF / CLEANER / CASHIER
        }

        if (req.username() != null) {
            // Liên kết lại user theo username (nếu không tồn tại sẽ ném lỗi)
            User user = userRepository.findByUsername(req.username())
                    .orElseThrow(() -> new RestaurantException("User not found"));
            staff.setUser(user);
        }

        staffRepository.save(staff);
        return toResponse(staff);
    }

    @Override
    public UserProfileResponse getProfile(Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getPrincipal();
        Long userId = credentialPayload.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestaurantException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }


    /** Nếu vẫn cần method này ở nơi khác — giữ nguyên, hiện không encode password tại đây */
    @Override
    public User updateStaffInfo(String username, UpdateStaffRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RestaurantException("User not found"));
        // Không đổi mật khẩu ở đây
        return userRepository.save(user);
    }

    /** XÓA CỨNG: xóa Staff và User liên kết (nếu có). Không còn check store. */
    @Transactional
    @Override
    public void deleteStaff(Long staffId, Authentication authentication) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        User linkedUser = staff.getUser();
        if (linkedUser != null) {
            // Cắt liên kết trước để tránh lỗi FK, rồi xóa user
            staff.setUser(null);
            staffRepository.save(staff);
            userRepository.delete(linkedUser);
        }

        staffRepository.delete(staff);
    }

    /** Helper: map entity -> response */
    private StaffResponse toResponse(Staff s) {
        return StaffResponse.builder()
                .id(s.getId())
                .fullName(s.getFullName())
                .email(s.getEmail())
                .phoneNumber(s.getPhoneNumber())
                .role(s.getRole())
                .userId(s.getUser() != null ? s.getUser().getId() : null)
                .createdAt(s.getCreatedAt())
                .build();
    }

    private String extractUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new RestaurantException("Invalid email format");
        }
        return email.split("@")[0];
    }

    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;

        // Kiểm tra username đã tồn tại chưa
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;

            // Giới hạn số lần thử để tránh vòng lặp vô hạn
            if (counter > 100) {
                throw new RestaurantException("Cannot generate unique username for: " + baseUsername);
            }
        }
        return username;
    }
}
