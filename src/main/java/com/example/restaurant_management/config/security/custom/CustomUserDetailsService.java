package com.example.restaurant_management.config.security.custom;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.config.security.user.UserDetailsImpl;
import com.example.restaurant_management.constant.RoleConstant;
import com.example.restaurant_management.entity.*;
import com.example.restaurant_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).
                orElseThrow(() -> new RestaurantException(ErrorEnum.USER_NOT_FOUND));

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getHashedPassword())
                .userId(user.getId())
                .enabled(Boolean.FALSE.equals(user.isDeleted()))
                .authorities(getUserAuthorities(user))
                .build();
    }

    private List<SimpleGrantedAuthority> getUserAuthorities(User user) {
        Set<UserRole> userRoles = userRoleRepository.findAllByUserId(user.getId());

        Set<Role> roles = roleRepository.findAllByIdIn(
                userRoles.stream()
                        .map(UserRole::getRoleId)
                        .collect(Collectors.toSet())
        );

        List<SimpleGrantedAuthority> roleAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(RoleConstant.ROLE_PREFIX + role.getName()))
                .toList();

        List<SimpleGrantedAuthority> permissionAuthorities;

        if (roleAuthorities.stream()
                .anyMatch(item -> item.getAuthority().equals(RoleConstant.ADMIN))) {
            permissionAuthorities = permissionRepository.findAll().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .toList();
        } else {
            Set<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleIdIn(
                    roles.stream()
                            .map(Role::getId)
                            .collect(Collectors.toSet())
            );

            Set<Permission> permissions = permissionRepository.findAllByIdIn(
                    rolePermissions.stream()
                            .map(RolePermission::getPermissionId)
                            .collect(Collectors.toSet())
            );

            permissionAuthorities = permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .toList();
        }

        return Stream.concat(roleAuthorities.stream(), permissionAuthorities.stream()).toList();
    }
}