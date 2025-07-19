package com.example.demo_innocode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LocationResponseDTO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String description;
    private String imageUrl;
    private String rating;
    private String checkIns;
    private String category;
}
