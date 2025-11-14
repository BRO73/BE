package com.example.restaurant_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffResponse {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean isActivated;
    private LocalDateTime createdAt;
    private Long userId;
}