package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRoleService {
    List<UserRole> getAllUserRoles();
    Optional<UserRole> getUserRoleById(Long id);
    UserRole createUserRole(UserRole userRole);
    UserRole updateUserRole(Long id, UserRole userRole);
    void deleteUserRole(Long id);
    List<UserRole> getUserRolesByUserId(Long userId);
    List<UserRole> getUserRolesByRoleId(Long roleId);
    Set<UserRole> getUserRolesByUserIds(Set<Long> userIds);
}
