package com.example.demo_innocode.dto.response;

import lombok.Builder;

import java.util.Set;

@Builder
public record UserResponse (
        Long id,
        String email,
        String fullname,
        String phone,
        String avatar,
        String location,
        String bio
){}