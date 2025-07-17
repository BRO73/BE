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
    private String name;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String imageUrl;

}
