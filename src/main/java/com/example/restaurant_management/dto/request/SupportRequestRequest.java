package com.example.restaurant_management.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportRequestRequest {
    private Long tableId;
    private String requestType;
    private String status;
    private String details;
    private Long staffId;
    private LocalDateTime resolvedAt;
}
