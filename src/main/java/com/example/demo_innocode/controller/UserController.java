package com.example.demo_innocode.controller;

import com.example.demo_innocode.dto.response.ActivityHistoryResponseDTO;
import com.example.demo_innocode.dto.response.UserResponse;
import com.example.demo_innocode.service.ActivityHistoryService;
import com.example.demo_innocode.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final ActivityHistoryService activityHistoryService;

    //Demo nam o day
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getUserLoginDetails(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{userId}/activity-history")
    public ResponseEntity<List<ActivityHistoryResponseDTO>> getActivityHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(activityHistoryService.getActivityHistoryByUserId(userId));
    }
}