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
public class BookingRequest {
    private Long tableId;
    private String customerName;
    private String customerPhone;
    private LocalDateTime bookingTime;
    private Integer numGuests;
    private String status;
    private String notes;
    private Long staffId;
}
