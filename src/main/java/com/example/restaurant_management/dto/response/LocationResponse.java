package com.example.restaurant_management.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    private Long id;
    private String name;
    private String description;
}
