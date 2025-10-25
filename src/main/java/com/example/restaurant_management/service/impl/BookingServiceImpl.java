package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.BookingCreateRequest;
import com.example.restaurant_management.dto.request.BookingUpdateRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.TableAvailabilityResponse;
import com.example.restaurant_management.entity.Booking;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.mapper.BookingMapper;
import com.example.restaurant_management.repository.BookingRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponse createBooking(BookingCreateRequest request) {
        TableEntity table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));

        List<Booking> existing = bookingRepository.findByTableAndDay(
                request.getTableId(),
                request.getBookingTime()
        );
        if (!existing.isEmpty()) {
            throw new IllegalStateException("Table already has a booking on this day!");
        }

        if (request.getCustomerName() == null || request.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (request.getCustomerPhone() == null || !request.getCustomerPhone().matches("\\d{9,15}")) {
            throw new IllegalArgumentException("Invalid phone number");
        }

        User customerUser = null;
        if (request.getCustomerUserId() != null) {
            customerUser = userRepository.findById(request.getCustomerUserId())
                    .orElse(null);
        }

        Booking booking = bookingMapper.toEntity(request, table, customerUser);
        booking.setStatus("Pending");

        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toResponse(saved);
    }

    @Override
    public BookingResponse updateBooking(Long id, BookingUpdateRequest request) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        TableEntity newTable = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));

        if (!existing.getTable().getId().equals(request.getTableId()) ||
                !existing.getBookingTime().toLocalDate().equals(request.getBookingTime().toLocalDate())) {

            List<Booking> conflicts = bookingRepository.findByTableAndDay(
                    request.getTableId(),
                    request.getBookingTime()
            );
            if (!conflicts.isEmpty() && !conflicts.get(0).getId().equals(id)) {
                throw new IllegalStateException("This table already has a booking on that day");
            }
        }

        bookingMapper.updateEntityFromRequest(existing, request, newTable);
        Booking updated = bookingRepository.save(existing);
        return bookingMapper.toResponse(updated);
    }

    @Override
    public void deleteBooking(Long id) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if ("Completed".equalsIgnoreCase(existing.getStatus())) {
            throw new IllegalStateException("Cannot delete a completed booking");
        }

        bookingRepository.delete(existing);
    }

    @Override
    public BookingResponse updateStatus(Long id, String status) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        switch (status) {
            case "Pending":
            case "Confirmed":
            case "Cancelled":
            case "Completed":
                existing.setStatus(status);
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + status);
        }

        Booking updated = bookingRepository.save(existing);
        return bookingMapper.toResponse(updated);
    }

    @Override
    public TableAvailabilityResponse isTableAvailable(Long tableId, LocalDateTime bookingTime) {
        if (!tableRepository.existsById(tableId)) {
            throw new EntityNotFoundException("Table not found");
        }

        List<Booking> sameDayBookings = bookingRepository.findByTableAndDay(tableId, bookingTime);
        boolean available = sameDayBookings.isEmpty();

        return TableAvailabilityResponse.builder()
                .tableId(tableId)
                .available(available)
                .message(available ? "Table is available" : "Table is already booked on this day")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookingMapper.toResponseList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomer(Long customerUserId) {
        List<Booking> bookings = bookingRepository.findByCustomerUserId(customerUserId);
        return bookingMapper.toResponseList(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        return bookingMapper.toResponse(booking);
    }
}