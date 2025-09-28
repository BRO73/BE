package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.RolePermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolePermissionResponse {
    private Long id;
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

    public static RolePermissionResponse fromEntity(RolePermission rolePermission) {
        return RolePermissionResponse.builder()
                .id(rolePermission.getId())
                .roleId(rolePermission.getRoleId())
                .permissionId(rolePermission.getPermissionId())
                .createdAt(rolePermission.getCreatedAt())
                .updatedAt(rolePermission.getUpdatedAt())
                .deleted(rolePermission.isDeleted())
                .activated(rolePermission.isActivated())
                .build();
    }
}
