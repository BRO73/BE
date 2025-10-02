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

}
