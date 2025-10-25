package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.BookingCreateRequest;
import com.example.restaurant_management.dto.request.BookingUpdateRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.TableAvailabilityResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingCreateRequest request);

    BookingResponse updateBooking(Long id, BookingUpdateRequest request);


    void deleteBooking(Long id);

    BookingResponse updateStatus(Long id, String status);

    TableAvailabilityResponse isTableAvailable(Long tableId, LocalDateTime bookingTime);

    List<BookingResponse> getAllBookings();

    List<BookingResponse> getBookingsByCustomer(Long customerUserId);

    BookingResponse getBookingById(Long id);
}