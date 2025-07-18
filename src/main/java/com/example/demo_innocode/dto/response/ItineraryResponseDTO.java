package com.example.demo_innocode.dto.response;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ItineraryResponseDTO(
        Long id,
        String title,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
