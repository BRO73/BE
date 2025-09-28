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

    public static BookingResponse fromEntity(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .tableId(booking.getTable().getId())
                .tableNumber(booking.getTable().getTableNumber())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .bookingTime(booking.getBookingTime())
                .numGuests(booking.getNumGuests())
                .status(booking.getStatus())
                .notes(booking.getNotes())
                .staffId(booking.getStaff() != null ? booking.getStaff().getId() : null)
                .staffName(booking.getStaff() != null ? booking.getStaff().getFullName() : null)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .deleted(booking.isDeleted())
                .activated(booking.isActivated())
                .build();
    }
}
