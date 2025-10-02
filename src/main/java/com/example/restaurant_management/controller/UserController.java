package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.UpdateStaffRequest;
import com.example.restaurant_management.dto.response.RestaurantResponse;
import com.example.restaurant_management.dto.response.UserProfileResponse;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'WAITSTAFF')")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantResponse<User>> updateStaff(
            @AuthenticationPrincipal String username,
            @RequestBody UpdateStaffRequest request
    ) {
        User updatedUser = userService.updateStaffInfo(username, request);
        return RestaurantResponse.ok(updatedUser, "Staff information updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<User> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        return userService.getUserByPhoneNumber(phoneNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<User>> getUsersByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUsersByEmail(email));
    }

    @GetMapping("/profile")
    public ResponseEntity<RestaurantResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal String username
    ) {
        UserProfileResponse profile = userService.getProfile(username);
        return RestaurantResponse.ok(profile, "Staff profile fetched successfully");
    }
}
