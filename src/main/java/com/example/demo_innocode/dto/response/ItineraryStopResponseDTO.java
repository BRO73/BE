package com.example.demo_innocode.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ItineraryStopResponseDTO(
        Long id,
        Long itineraryId,
        Long locationId,
        int stopOrder,
        LocalDateTime startedAt,
        LocalDateTime endedAt

) {}
