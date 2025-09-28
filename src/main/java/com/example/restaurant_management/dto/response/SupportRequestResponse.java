package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.SupportRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestResponse {
    private Long id;
    private Long tableId;
    private String tableNumber;
    private String requestType;
    private String status;
    private String details;
    private Long staffId;
    private String staffName;
    private LocalDateTime resolvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

    public static SupportRequestResponse fromEntity(SupportRequest supportRequest) {
        return SupportRequestResponse.builder()
                .id(supportRequest.getId())
                .tableId(supportRequest.getTable().getId())
                .tableNumber(supportRequest.getTable().getTableNumber())
                .requestType(supportRequest.getRequestType())
                .status(supportRequest.getStatus())
                .details(supportRequest.getDetails())
                .staffId(supportRequest.getStaff() != null ? supportRequest.getStaff().getId() : null)
                .staffName(supportRequest.getStaff() != null ? supportRequest.getStaff().getFullName() : null)
                .resolvedAt(supportRequest.getResolvedAt())
                .createdAt(supportRequest.getCreatedAt())
                .updatedAt(supportRequest.getUpdatedAt())
                .deleted(supportRequest.isDeleted())
                .activated(supportRequest.isActivated())
                .build();
    }
}
