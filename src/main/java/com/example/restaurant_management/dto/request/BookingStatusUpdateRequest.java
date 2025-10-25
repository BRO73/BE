package com.example.restaurant_management.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingStatusUpdateRequest {
    private String status;
}
