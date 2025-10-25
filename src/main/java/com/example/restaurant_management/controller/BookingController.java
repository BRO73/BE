package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.BookingCreateRequest;
import com.example.restaurant_management.dto.request.BookingStatusUpdateRequest;
import com.example.restaurant_management.dto.request.BookingUpdateRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.TableAvailabilityResponse;
import com.example.restaurant_management.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN','WAITSTAFF')")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/customer/{customerUserId}")
    public ResponseEntity<List<BookingResponse>> getBookingsByCustomer(@PathVariable Long customerUserId) {
        List<BookingResponse> bookings = bookingService.getBookingsByCustomer(customerUserId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@RequestBody BookingCreateRequest request) {
        BookingResponse created = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @RequestBody BookingUpdateRequest request) {
        BookingResponse updated = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
//    @PreAuthorize("hasAnyRole('ADMIN','WAITSTAFF')")
    public ResponseEntity<BookingResponse> updateBookingStatus(
            @PathVariable Long id,
            @RequestBody BookingStatusUpdateRequest request) {
        BookingResponse updated = bookingService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/table/{tableId}/available")
    public ResponseEntity<TableAvailabilityResponse> checkTableAvailability(
            @PathVariable Long tableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookingTime) {
        TableAvailabilityResponse response = bookingService.isTableAvailable(tableId, bookingTime);
        return ResponseEntity.ok(response);
    }
}