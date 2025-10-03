package com.example.restaurant_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationRequest {

    @NotBlank(message = "Location name must not be blank")
    @Size(max = 100, message = "Location name must be less than 100 characters")
    private String name;

    private String description;
}

