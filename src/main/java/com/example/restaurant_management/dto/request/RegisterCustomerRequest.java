package com.example.restaurant_management.dto.request;

import lombok.Data;

@Data
public class RegisterCustomerRequest {
    private String phoneNumber;
    private String fullName;
    private String email;
    private String address;
}
