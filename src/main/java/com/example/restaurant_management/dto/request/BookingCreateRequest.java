package com.example.restaurant_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateRequest {
    private String customerName;
    private String customerPhone;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingTime;

    private Integer numGuests;
    private String notes;
    private Long tableId;
    private Long customerUserId; // Optional
}