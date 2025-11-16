package com.example.restaurant_management.dto.request;

import lombok.*;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MergeOrderRequest {

    @NotNull(message = "Source order ID is required")
    private Long sourceOrderId;

    @NotNull(message = "Target order ID is required")
    private Long targetOrderId;
}
