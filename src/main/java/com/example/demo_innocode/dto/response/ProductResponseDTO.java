package com.example.demo_innocode.dto.response;

import lombok.*;
import java.math.BigDecimal;

@Builder
public record ProductResponseDTO(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String type,
        String image,
        Boolean featured,
        String village,
        Long userId
) {}
