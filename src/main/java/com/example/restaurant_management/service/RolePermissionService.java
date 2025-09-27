package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.RolePermission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RolePermissionService {
    List<RolePermission> getAllRolePermissions();
    Optional<RolePermission> getRolePermissionById(Long id);
    RolePermission createRolePermission(RolePermission rolePermission);
    RolePermission updateRolePermission(Long id, RolePermission rolePermission);
    void deleteRolePermission(Long id);
    List<RolePermission> getRolePermissionsByRoleId(Long roleId);
    List<RolePermission> getRolePermissionsByPermissionId(Long permissionId);
    Set<RolePermission> getRolePermissionsByRoleIds(Set<Long> roleIds);
}
