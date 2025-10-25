package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.dto.response.CustomerSimpleResponse;
import com.example.restaurant_management.dto.response.TableSimpleResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long id;
    private String customerName;
    private String customerPhone;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bookingTime;

    private Integer numGuests;
    private String notes;
    private String status;

    private TableSimpleResponse table;
    private CustomerSimpleResponse customer;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}