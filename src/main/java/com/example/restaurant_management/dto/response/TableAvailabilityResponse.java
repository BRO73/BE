package com.example.restaurant_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableAvailabilityResponse {
    private Long tableId;
    private boolean available;
    private String message;
}
