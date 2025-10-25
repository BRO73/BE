package com.example.restaurant_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSimpleResponse {
    private Long id;
    private String tableNumber;
    private Integer capacity;
    private String status;
}
