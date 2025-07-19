package com.example.demo_innocode.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ItineraryRequestDTO {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
