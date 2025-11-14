package com.example.restaurant_management.dto.request;

import lombok.Data;

@Data
public class CustomerRequest {
    private String fullName;
    private String email;
    private String phone;
    private Boolean activated;
}
