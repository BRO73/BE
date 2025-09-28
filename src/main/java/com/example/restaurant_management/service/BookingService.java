package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingService {

    List<Booking> getAllBookings();

    Optional<Booking> getBookingById(Long id);

    Booking createBooking(Booking booking);

    Booking updateBooking(Long id, Booking booking);

    void deleteBooking(Long id);

    List<Booking> getBookingsByStatus(String status);

    List<Booking> getBookingsBetween(LocalDateTime start, LocalDateTime end);

    List<Booking> getBookingsByCustomerPhone(String phone);
}
