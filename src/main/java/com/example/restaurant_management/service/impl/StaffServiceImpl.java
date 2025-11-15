package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.request.UpdateProfileRequest;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.StaffProfileResponse;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.mapper.StaffMapper;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.StaffRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.StaffService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final StaffMapper staffMapper;

    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public User updateStaffInfo(String username, UpdateStaffRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RestaurantException("User not found with username: " + username));

        Staff staff = staffRepository.findByUser(user)
                .orElseThrow(() -> new RestaurantException("Staff not found for user: " + username));

        staff.setFullName(request.fullName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());

        staffRepository.save(staff);
        return user;
    }

    @Override
    public StaffProfileResponse getProfile(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RestaurantException("User not found"));

        Staff staff = staffRepository.findByUser(user)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        // 👇 Lấy danh sách role theo userId
        java.util.Set<String> roleNames = userRoleRepository.findAllByUserId(user.getId())
                .stream()
                .map(ur -> roleRepository.findById(ur.getRoleId())
                        .orElseThrow(() -> new RestaurantException("Role not found for id: " + ur.getRoleId()))
                        .getName())
                .collect(java.util.stream.Collectors.toSet());

        // 👇 Gọi mapper với ĐỦ 6 tham số
        return staffMapper.toProfileResponse(
                user.getId(),
                user.getUsername(),
                staff.getFullName(),
                staff.getEmail(),
                staff.getPhoneNumber(),
                roleNames
        );
    }

    @Override
    @Transactional
    public StaffProfileResponse updateProfile(String username, UpdateProfileRequest request) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RestaurantException("User not found"));

        Staff staff = staffRepository.findByUser(user)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        if (request.fullName() != null) staff.setFullName(request.fullName());
        if (request.email() != null) staff.setEmail(request.email());
        if (request.phoneNumber() != null) staff.setPhoneNumber(request.phoneNumber());

        staffRepository.save(staff);

        // Lấy roles
        java.util.Set<String> roleNames = userRoleRepository.findAllByUserId(user.getId())
                .stream()
                .map(ur -> roleRepository.findById(ur.getRoleId())
                        .orElseThrow(() -> new RestaurantException("Role not found"))
                        .getName())
                .collect(java.util.stream.Collectors.toSet());

        return staffMapper.toProfileResponse(
                user.getId(),
                user.getUsername(),
                staff.getFullName(),
                staff.getEmail(),
                staff.getPhoneNumber(),
                roleNames
        );
    }

    @Override
    public List<StaffResponse> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();

        return staffList.stream().map(staff -> {

            Long userId = staff.getUser() != null ? staff.getUser().getId() : null;

            Set<String> roles;

            if (userId != null) {
                // Lấy role thật từ UserRole
                roles = userRoleRepository.findAllByUserId(userId)
                        .stream()
                        .map(ur -> roleRepository.findById(ur.getRoleId())
                                .orElseThrow(() -> new RestaurantException("Role not found"))
                                .getName())
                        .collect(Collectors.toSet());
            } else {
                // Chưa có user → dùng role String lưu trong staff table
                roles = Set.of(staff.getRole());
            }

            return staffMapper.toResponse(staff, roles);
        }).collect(Collectors.toList());
    }


    @Override
    @Transactional
    public StaffResponse createStaff(CreateStaffRequest request) {
        Staff staff = new Staff();
        staff.setFullName(request.fullName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());
        staff.setRole(request.role()); // role = String

        staffRepository.save(staff);

        return staffMapper.toResponse(staff, Set.of(request.role()));
    }

    // 🟡 UPDATE
    @Override
    @Transactional
    public StaffResponse updateStaff(Long id, UpdateStaffRequest request) {

        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        staff.setFullName(request.fullName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());

        if (request.role() != null)
            staff.setRole(request.role());

        staffRepository.save(staff);

        Set<String> roleNames;

        if (staff.getUser() != null) {
            Long userId = staff.getUser().getId();
            roleNames = userRoleRepository.findAllByUserId(userId)
                    .stream()
                    .map(ur -> roleRepository.findById(ur.getRoleId())
                            .orElseThrow(() -> new RestaurantException("Role not found"))
                            .getName())
                    .collect(Collectors.toSet());
        } else {
            roleNames = Set.of(staff.getRole());
        }

        return staffMapper.toResponse(staff, roleNames);
    }


    // 🔴 DELETE
    @Override
    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RestaurantException("Staff not found"));
        staffRepository.delete(staff);
    }

}