package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Permission;
import com.example.restaurant_management.repository.PermissionRepository;
import com.example.restaurant_management.service.PermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;

    public PermissionServiceImpl(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    public Optional<Permission> getPermissionById(Long id) {
        return permissionRepository.findById(id);
    }

    @Override
    public Permission createPermission(Permission permission) {
        return permissionRepository.save(permission);
    }

    @Override
    public Permission updatePermission(Long id, Permission permission) {
        permission.setId(id);
        return permissionRepository.save(permission);
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    @Override
    public Optional<Permission> getPermissionByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public List<Permission> getPermissionsByIds(List<Long> ids) {
        return permissionRepository.findAllByIdIn(ids);
    }
}
