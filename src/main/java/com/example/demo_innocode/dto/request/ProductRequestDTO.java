package com.example.demo_innocode.dto.request;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {
    private String name;
    private String description;
    private BigDecimal price;
    private String type;
    private String image;
    private Boolean featured;
    private String village;
    private Long userId;
}
