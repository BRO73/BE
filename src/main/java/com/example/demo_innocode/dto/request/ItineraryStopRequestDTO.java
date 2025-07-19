package com.example.demo_innocode.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItineraryStopRequestDTO {
    private Long itineraryId;
    private Long locationId;
    private int stopOrder;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

}
