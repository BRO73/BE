package com.example.demo_innocode.dto.request;

import com.example.demo_innocode.constant.LocationType;
import com.example.demo_innocode.entity.VirtualTour;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
public class LocationRequestDTO {
    private String name;
    private LocationType type;
    private String description;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private VirtualTour virtualTour;
}
