package com.example.restaurant_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerSimpleResponse {
    private Long id;
    private String username;
}