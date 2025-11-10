package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.response.CustomerResponse;
import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(Long id);

    List<CustomerResponse> searchCustomers(String keyword);

    void deleteCustomer(Long id);

    boolean existsByPhoneNumber(String phoneNumber);
}
