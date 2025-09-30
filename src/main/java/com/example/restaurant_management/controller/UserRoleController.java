package com.example.restaurant_management.controller;

import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.service.UserRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/user-roles")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    @GetMapping
    public ResponseEntity<List<UserRole>> getAllUserRoles() {
        return ResponseEntity.ok(userRoleService.getAllUserRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRole> getUserRoleById(@PathVariable Long id) {
        return userRoleService.getUserRoleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserRole> createUserRole(@RequestBody UserRole userRole) {
        return ResponseEntity.ok(userRoleService.createUserRole(userRole));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRole> updateUserRole(@PathVariable Long id, @RequestBody UserRole userRole) {
        return ResponseEntity.ok(userRoleService.updateUserRole(id, userRole));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserRole(@PathVariable Long id) {
        userRoleService.deleteUserRole(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserRole>> getUserRolesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(userRoleService.getUserRolesByUserId(userId));
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<UserRole>> getUserRolesByRoleId(@PathVariable Long roleId) {
        return ResponseEntity.ok(userRoleService.getUserRolesByRoleId(roleId));
    }
}
