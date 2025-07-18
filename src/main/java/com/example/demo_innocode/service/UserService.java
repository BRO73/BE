package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.response.UserResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {
    public UserResponse getCurrentUser(Authentication authentication);
    List<UserResponse> findAll();
}