package com.example.restaurant_management.controller;

import com.example.restaurant_management.entity.RolePermission;
import com.example.restaurant_management.service.RolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role-permissions")
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;

    public RolePermissionController(RolePermissionService rolePermissionService) {
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping
    public ResponseEntity<List<RolePermission>> getAllRolePermissions() {
        return ResponseEntity.ok(rolePermissionService.getAllRolePermissions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RolePermission> getRolePermissionById(@PathVariable Long id) {
        return rolePermissionService.getRolePermissionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RolePermission> createRolePermission(@RequestBody RolePermission rolePermission) {
        return ResponseEntity.ok(rolePermissionService.createRolePermission(rolePermission));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolePermission> updateRolePermission(@PathVariable Long id, @RequestBody RolePermission rolePermission) {
        return ResponseEntity.ok(rolePermissionService.updateRolePermission(id, rolePermission));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRolePermission(@PathVariable Long id) {
        rolePermissionService.deleteRolePermission(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<RolePermission>> getRolePermissionsByRoleId(@PathVariable Long roleId) {
        return ResponseEntity.ok(rolePermissionService.getRolePermissionsByRoleId(roleId));
    }

    @GetMapping("/permission/{permissionId}")
    public ResponseEntity<List<RolePermission>> getRolePermissionsByPermissionId(@PathVariable Long permissionId) {
        return ResponseEntity.ok(rolePermissionService.getRolePermissionsByPermissionId(permissionId));
    }
}
