package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long id;
    private Long tableId;
    private String tableNumber;
    private String customerName;
    private String customerPhone;
    private LocalDateTime bookingTime;
    private Integer numGuests;
    private String status;
    private String notes;
    private Long staffId;
    private String staffName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private boolean activated;

}
