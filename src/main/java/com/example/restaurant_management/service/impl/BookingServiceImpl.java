package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Booking;
import com.example.restaurant_management.repository.BookingRepository;
import com.example.restaurant_management.service.BookingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    public Booking createBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Long id, Booking booking) {
        booking.setId(id);
        return bookingRepository.save(booking);
    }

    @Override
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    @Override
    public List<Booking> getBookingsByStatus(String status) {
        return bookingRepository.findByStatus(status);
    }

    @Override
    public List<Booking> getBookingsBetween(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByBookingTimeBetween(start, end);
    }

    @Override
    public List<Booking> getBookingsByCustomerPhone(String phone) {
        return bookingRepository.findByCustomerPhone(phone);
    }
}
