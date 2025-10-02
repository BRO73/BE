package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.CreateStaffRequest;
import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.StaffResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.Store;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.StaffRepository;
import com.example.restaurant_management.repository.StoreRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffRepository staffRepository;
    private final StoreRepository storeRepository;

    @Override
    public List<StaffResponse> getAllStaffInStore(Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        String storeName = credentialPayload.getStoreName();
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new RestaurantException(ErrorEnum.STORE_NOT_FOUND));
        List<Staff> staffList = staffRepository.findAllByStore(store);

        List<StaffResponse> staffResponseList = new ArrayList<>();
        for (Staff staff : staffList) {
            StaffResponse staffResponse = StaffResponse.builder()
                    .id(staff.getId())
                    .fullName(staff.getFullName())
                    .email(staff.getEmail())
                    .phoneNumber(staff.getPhoneNumber())
                    .isActivated(staff.isActivated())
                    .createdAt(staff.getCreatedAt())
                    .storeName(storeName)
                    .userId(staff.getUser() != null ? staff.getUser().getId() : null)
                    .build();
            staffResponseList.add(staffResponse);
        }
        return staffResponseList;
    }

    @Override
    public StaffResponse createStaff(CreateStaffRequest createStaffRequest, Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        String storeName = credentialPayload.getStoreName();
        Staff staff = Staff.builder()
                .fullName(createStaffRequest.fullName())
                .email(createStaffRequest.email())
                .phoneNumber(createStaffRequest.phoneNumber())
                .store(storeRepository.findByName(storeName)
                        .orElseThrow(() -> new RestaurantException(ErrorEnum.STORE_NOT_FOUND)))
                .build();
        staffRepository.save(staff);

        return StaffResponse.builder()
                .id(staff.getId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .isActivated(staff.isActivated())
                .createdAt(staff.getCreatedAt())
                .storeName(storeName)
                .userId(staff.getUser() != null ? staff.getUser().getId() : null)
                .build();
    }

    @Override
    public StaffResponse updateStaff(Long staffId, UpdateStaffRequest request, Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        String storeName = credentialPayload.getStoreName();

        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new RestaurantException(ErrorEnum.STORE_NOT_FOUND));

        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RestaurantException("Staff not found"));

        if (!staff.getStore().getId().equals(store.getId())) {
            throw new RestaurantException(ErrorEnum.ACCESS_DENIED);
        }

        staff.setFullName(request.fullName());
        staff.setEmail(request.email());
        staff.setPhoneNumber(request.phoneNumber());

        if (request.isActivated() != null) {
            staff.setActivated(request.isActivated());
        }

        if (request.username() != null) {
            staff.setUser(userRepository.findStaffByUsernameAndStore(request.username(), store)
                    .orElseThrow(() -> new RestaurantException(ErrorEnum.USER_NOT_FOUND)));
        }

        staffRepository.save(staff);

        return StaffResponse.builder()
                .id(staff.getId())
                .fullName(staff.getFullName())
                .email(staff.getEmail())
                .phoneNumber(staff.getPhoneNumber())
                .isActivated(staff.isActivated())
                .createdAt(staff.getCreatedAt())
                .storeName(storeName)
                .userId((staff.getUser() != null) ? staff.getUser().getId() : null)
                .build();
    }

    @Override
    public UserProfileResponse getProfile(Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        Long userId = credentialPayload.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }

    @Override
    public User updateStaffInfo(String username, UpdateStaffRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        if (request.password() != null && !request.password().isBlank()) {
//            user.setHashedPassword(passwordEncoder.encode(request.password()));
//        }

        return userRepository.save(user);
    }


}
