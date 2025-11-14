package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.CustomerResponse;
import com.example.restaurant_management.service.BookingService;
import com.example.restaurant_management.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CustomerResponse>> searchCustomers(@RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(customerService.searchCustomers(keyword));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/verify-phone")
    public ResponseEntity<Boolean> verifyCustomerPhone(@RequestParam String phoneNumber) {
        boolean exists = customerService.existsByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(exists);
    }


    @GetMapping("/by-phone")
    public ResponseEntity<CustomerResponse> getCustomerByPhone(@RequestParam String phone) {
        CustomerResponse customer = customerService.findByPhoneNumber(phone);
        return ResponseEntity.ok(customer);
    }
    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByCustomer(@PathVariable Long id) {
        List<BookingResponse> bookings = bookingService.getBookingsByCustomerId(id);
        return ResponseEntity.ok(bookings);
    }



}
