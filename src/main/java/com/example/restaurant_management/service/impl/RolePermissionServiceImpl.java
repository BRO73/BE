package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.RolePermission;
import com.example.restaurant_management.repository.RolePermissionRepository;
import com.example.restaurant_management.service.RolePermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;

    public RolePermissionServiceImpl(RolePermissionRepository rolePermissionRepository) {
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Override
    public List<RolePermission> getAllRolePermissions() {
        return rolePermissionRepository.findAll();
    }

    @Override
    public Optional<RolePermission> getRolePermissionById(Long id) {
        return rolePermissionRepository.findById(id);
    }

    @Override
    public RolePermission createRolePermission(RolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }

    @Override
    public RolePermission updateRolePermission(Long id, RolePermission rolePermission) {
        rolePermission.setId(id);
        return rolePermissionRepository.save(rolePermission);
    }

    @Override
    public void deleteRolePermission(Long id) {
        rolePermissionRepository.deleteById(id);
    }

    @Override
    public List<RolePermission> getRolePermissionsByRoleId(Long roleId) {
        return rolePermissionRepository.findAllByRoleId(roleId);
    }

    @Override
    public List<RolePermission> getRolePermissionsByPermissionId(Long permissionId) {
        return rolePermissionRepository.findAllByPermissionId(permissionId);
    }

    @Override
    public Set<RolePermission> getRolePermissionsByRoleIds(Set<Long> roleIds) {
        return rolePermissionRepository.findAllByRoleIdIn(roleIds);
    }
}
