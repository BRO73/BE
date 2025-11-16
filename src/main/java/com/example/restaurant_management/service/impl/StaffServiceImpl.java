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
import java.util.Map;
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
    public List<StaffResponse> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();

        // ✅ Lấy tất cả UserRole + Role 1 lần
        Map<Long, Set<Long>> userIdRoleIdsMap = userRoleRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(
                        ur -> ur.getUserId(),
                        Collectors.mapping(ur -> ur.getRoleId(), Collectors.toSet())
                ));

        Map<Long, String> roleIdNameMap = roleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(r -> r.getId(), r -> r.getName()));

        return staffList.stream().map(staff -> {
            Long userId = staff.getUser() != null ? staff.getUser().getId() : null;
            Set<String> roleNames;
            if (userId != null && userIdRoleIdsMap.containsKey(userId)) {
                roleNames = userIdRoleIdsMap.get(userId)
                        .stream()
                        .map(roleIdNameMap::get)
                        .collect(Collectors.toSet());
            } else {
                roleNames = Set.of(staff.getRole());
            }
            return staffMapper.toResponse(staff, roleNames);
        }).collect(Collectors.toList());
    }

    @Override
    public StaffProfileResponse getProfile(Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RestaurantException("User not found"));

        Staff staff = staffRepository.findByUser(user)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        // ✅ Lấy tất cả roles 1 lần → tránh N+1
        Map<Long, String> roleIdNameMap = roleRepository.findAll()
                .stream()
                .collect(Collectors.toMap(r -> r.getId(), r -> r.getName()));

        Set<String> roleNames = userRoleRepository.findAllByUserId(user.getId())
                .stream()
                .map(ur -> roleIdNameMap.get(ur.getRoleId()))
                .collect(Collectors.toSet());

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

    @Override
    @Transactional
    public StaffResponse updateStaff(Long id, UpdateStaffRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        staff.setFullName(request.fullName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());
        if (request.role() != null) staff.setRole(request.role());

        staffRepository.save(staff);

        Set<String> roleNames;
        if (staff.getUser() != null) {
            // Lấy tất cả UserRole + Role 1 lần
            Map<Long, String> roleIdNameMap = roleRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(r -> r.getId(), r -> r.getName()));

            roleNames = userRoleRepository.findAllByUserId(staff.getUser().getId())
                    .stream()
                    .map(ur -> roleIdNameMap.get(ur.getRoleId()))
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