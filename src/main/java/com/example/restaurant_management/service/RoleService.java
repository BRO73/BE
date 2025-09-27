package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Long id);
    Role createRole(Role role);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
    Optional<Role> getRoleByName(String name);
    List<Role> getRolesByIds(List<Long> ids);
}
