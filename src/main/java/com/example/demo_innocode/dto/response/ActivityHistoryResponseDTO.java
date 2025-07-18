package com.example.demo_innocode.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ActivityHistoryResponseDTO(
        Long stopId,
        String locationName,
        LocalDateTime createdAt,
        Long itineraryId,
        String itineraryTitle
) {}
