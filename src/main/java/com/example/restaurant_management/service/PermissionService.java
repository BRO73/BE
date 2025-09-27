package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionService {
    List<Permission> getAllPermissions();
    Optional<Permission> getPermissionById(Long id);
    Permission createPermission(Permission permission);
    Permission updatePermission(Long id, Permission permission);
    void deletePermission(Long id);
    Optional<Permission> getPermissionByName(String name);
    List<Permission> getPermissionsByIds(List<Long> ids);
}
