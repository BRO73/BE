package com.example.restaurant_management.config.security.custom;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.config.security.user.UserDetailsImpl;
import com.example.restaurant_management.constant.RoleConstant;
import com.example.restaurant_management.entity.Permission;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.RolePermission;
import com.example.restaurant_management.entity.Store;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.repository.PermissionRepository;
import com.example.restaurant_management.repository.RolePermissionRepository;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.StoreRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
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

    /** Fallback khi chỉ có u_id trong token (hoặc username không khớp DB). */
    public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found by id: " + userId));

        return UserDetailsImpl.builder()
                .username(user.getUsername())
                .password(user.getHashedPassword())
                .userId(user.getId())
                .enabled(Boolean.FALSE.equals(user.isDeleted()))
                .authorities(getUserAuthorities(user))
                .storeName(null)
                .build();
    }

    private List<SimpleGrantedAuthority> getUserAuthorities(User user) {
        Set<UserRole> userRoles = userRoleRepository.findAllByUserId(user.getId());

        Set<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        Set<Role> roles = roleRepository.findAllByIdIn(roleIds);

        List<SimpleGrantedAuthority> roleAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(RoleConstant.ROLE_PREFIX + role.getName()))
                .toList();

        List<SimpleGrantedAuthority> permissionAuthorities;

        boolean isAdmin = roleAuthorities.stream()
                .anyMatch(item -> item.getAuthority().equals(RoleConstant.ADMIN));

        if (isAdmin) {
            permissionAuthorities = permissionRepository.findAll().stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .toList();
        } else {
            Set<Long> roleIdSet = roles.stream().map(Role::getId).collect(Collectors.toSet());
            Set<RolePermission> rolePermissions = rolePermissionRepository.findAllByRoleIdIn(roleIdSet);

            Set<Long> permissionIds = rolePermissions.stream()
                    .map(RolePermission::getPermissionId)
                    .collect(Collectors.toSet());

            Set<Permission> permissions = permissionRepository.findAllByIdIn(permissionIds);

            permissionAuthorities = permissions.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                    .toList();
        }

        return Stream.concat(roleAuthorities.stream(), permissionAuthorities.stream()).toList();
    }
}
