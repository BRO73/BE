package com.example.restaurant_management.dto.response;

import com.example.restaurant_management.entity.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpLoginResponse {
    private String status; // EXISTING_CUSTOMER, NEW_CUSTOMER, REGISTERED
    private String jwt;
    private Customer customer;
}
