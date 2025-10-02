package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.CreateUserRequest;
import com.example.restaurant_management.dto.request.UpdateUserRequest;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.dto.response.UserResponse;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.Store;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.mapper.UserMapper;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.StoreRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StoreRepository storeRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {

        List<User> userList = userRepository.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for (User user : userList) {
            UserResponse userResponse = userMapper.toResponse(user);
            userResponseList.add(userResponse);
        }
        return userResponseList;
    }

    @Override
    public List<UserResponse> getAllUserInStore(Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        String storeName = credentialPayload.getStoreName();
        Store store = storeRepository.findByName(storeName)
                .orElseThrow(() -> new RestaurantException(String.valueOf(ErrorEnum.STORE_NOT_FOUND)));

        List<User> userList = userRepository.findAllByStore(store);
        List<UserResponse> userResponseList = new ArrayList<>();

        for (User user : userList) {
            Set<Role> roles = roleRepository.findByUserId(user.getId());
            Set<String> roleNames = new HashSet<>();
            for (Role role : roles) {
                roleNames.add(role.getName());
            }

            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .role(roleNames)
                    .build();

            userResponseList.add(userResponse);
        }
        return userResponseList;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest createUserRequest, Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        User user = User.builder()
                .username(createUserRequest.username())
                .store(storeRepository.findByName(credentialPayload.getStoreName())
                        .orElseThrow(() -> new RestaurantException("Store not found")))
                .build();
        userRepository.save(user);

        Set<String> roleRequestList = createUserRequest.role();
        Set<String> roleResponseList = new HashSet<>();
        for (String roleRequest : roleRequestList) {
            Role role = roleRepository.findByName(roleRequest)
                    .orElseThrow(() -> new RestaurantException("Role not found"));

            UserRole userRole = UserRole.builder()
                    .roleId(role.getId())
                    .userId(user.getId())
                    .build();
            userRoleRepository.save(userRole);
            roleResponseList.add(role.getName());
        }

        return UserResponse.builder()
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .id(user.getId())
                .updatedAt(user.getUpdatedAt())
                .role(roleResponseList)
                .build();
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestaurantException("User not found"));

        if (request.username() != null && !request.username().isBlank()) {
            user.setUsername(request.username());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setHashedPassword(passwordEncoder.encode(request.password()));
        }

        userRoleRepository.deleteAllByUserId(user.getId());

        Set<String> roleRequestList = request.role();
        Set<String> roleResponseList = new HashSet<>();
        for (String roleRequest : roleRequestList) {
            Role role = roleRepository.findByName(roleRequest)
                    .orElseThrow(() -> new RestaurantException("Role not found"));

            UserRole userRole = UserRole.builder()
                    .roleId(role.getId())
                    .userId(user.getId())
                    .build();
            userRoleRepository.save(userRole);
            roleResponseList.add(role.getName());
        }

        userRepository.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .role(roleResponseList)
                .build();
    }


    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        // Note: This method needs to be added to UserRepository
        return Optional.empty();
    }

    @Override
    public List<User> getUsersByEmail(String email) {
        // Note: This method needs to be added to UserRepository
        return List.of();
    }

    @Override
    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }



}