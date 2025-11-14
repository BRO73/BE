package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.CustomerRequest;
import com.example.restaurant_management.dto.response.CustomerResponse;
import com.example.restaurant_management.entity.Customer;

import java.util.List;

public interface CustomerService {

    List<CustomerResponse> getAllCustomers();

    CustomerResponse getCustomerById(Long id);

    List<CustomerResponse> searchCustomers(String keyword);

    CustomerResponse findByPhoneNumber(String phoneNumber);

    void deleteCustomer(Long id);
    CustomerResponse updateCustomer(Long id, CustomerRequest customer);
    boolean existsByPhoneNumber(String phoneNumber);
}
