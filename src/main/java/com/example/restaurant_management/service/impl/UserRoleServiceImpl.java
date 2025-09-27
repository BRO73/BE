package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.UserRoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public List<UserRole> getAllUserRoles() {
        return userRoleRepository.findAll();
    }

    @Override
    public Optional<UserRole> getUserRoleById(Long id) {
        return userRoleRepository.findById(id);
    }

    @Override
    public UserRole createUserRole(UserRole userRole) {
        return userRoleRepository.save(userRole);
    }

    @Override
    public UserRole updateUserRole(Long id, UserRole userRole) {
        userRole.setId(id);
        return userRoleRepository.save(userRole);
    }

    @Override
    public void deleteUserRole(Long id) {
        userRoleRepository.deleteById(id);
    }

    @Override
    public List<UserRole> getUserRolesByUserId(Long userId) {
        Set<UserRole> userRoleSet = userRoleRepository.findAllByUserId(userId);
        return new ArrayList<>(userRoleSet);
    }

    @Override
    public List<UserRole> getUserRolesByRoleId(Long roleId) {
        return userRoleRepository.findAllByRoleId(roleId);
    }

    @Override
    public Set<UserRole> getUserRolesByUserIds(Set<Long> userIds) {
        return userRoleRepository.findAllByUserIdIn(userIds);
    }
}
