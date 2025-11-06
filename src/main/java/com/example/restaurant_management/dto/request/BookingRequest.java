package com.example.restaurant_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class BookingRequest {
    private List<Long> tableIds;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private Integer numGuests;
    private String status;
    private String notes;
    private Long staffId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingTime;

}
