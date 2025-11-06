package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.response.CustomerResponse;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Override
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        return mapToResponse(customer);
    }

    @Override
    public List<CustomerResponse> searchCustomers(String keyword) {
        // Nếu keyword trống/null thì trả về toàn bộ
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }

        return customerRepository.searchCustomers(keyword.trim())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        // ✅ Xóa User để cascade xóa Customer theo quan hệ trong DB
        User user = customer.getUser();
        if (user != null) {
            userRepository.delete(user);
        } else {
            customerRepository.delete(customer);
        }
    }

    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .userId(customer.getId())
                .fullName(customer.getFullName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .build();
    }
}
