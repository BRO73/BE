package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.common.constant.ErrorEnum;
import com.example.demo_innocode.common.exception.InnoException;
import com.example.demo_innocode.dto.response.UserResponse;
import com.example.demo_innocode.model.CredentialPayload;
import com.example.demo_innocode.repository.UserRepository;
import com.example.demo_innocode.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserResponse getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            throw new InnoException(ErrorEnum.ACCESS_DENIED);
        }

        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        Long id = credentialPayload.getUserId();

        return userRepository.findById(id)
                .map(user -> UserResponse.builder()
                        .fullname(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build()
                )
                .orElseThrow(() -> new InnoException(ErrorEnum.USER_NOT_FOUND));
    }


    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> UserResponse.builder()
                        .fullname(user.getFullName())
                        .email(user.getEmail())
                        .phone(user.getPhone())
                        .build())
                .collect(Collectors.toList());
    }
}
