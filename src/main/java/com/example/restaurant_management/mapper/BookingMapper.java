package com.example.restaurant_management.mapper;

import com.example.restaurant_management.dto.request.BookingCreateRequest;
import com.example.restaurant_management.dto.request.BookingUpdateRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.CustomerSimpleResponse;
import com.example.restaurant_management.dto.response.TableSimpleResponse;
import com.example.restaurant_management.entity.Booking;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BookingMapper {

    public Booking toEntity(BookingCreateRequest request, TableEntity table, User customerUser) {
        return Booking.builder()
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .bookingTime(request.getBookingTime())
                .numGuests(request.getNumGuests())
                .notes(request.getNotes())
                .table(table)
                .customerUser(customerUser)
                .build();
    }

    public BookingResponse toResponse(Booking booking) {
        BookingResponse.BookingResponseBuilder builder = BookingResponse.builder()
                .id(booking.getId())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .bookingTime(booking.getBookingTime())
                .numGuests(booking.getNumGuests())
                .notes(booking.getNotes())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt());

        if (booking.getTable() != null) {
            builder.table(TableSimpleResponse.builder()
                    .id(booking.getTable().getId())
                    .tableNumber(booking.getTable().getTableNumber())
                    .capacity(booking.getTable().getCapacity())
                    .status(booking.getTable().getStatus())
                    .build());
        }

        if (booking.getCustomerUser() != null) {
            builder.customer(CustomerSimpleResponse.builder()
                    .id(booking.getCustomerUser().getId())
                    .username(booking.getCustomerUser().getUsername())
                    .build());
        }

        return builder.build();
    }

    public List<BookingResponse> toResponseList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void updateEntityFromRequest(Booking booking, BookingUpdateRequest request, TableEntity table) {
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setBookingTime(request.getBookingTime());
        booking.setNumGuests(request.getNumGuests());
        booking.setNotes(request.getNotes());
        if (table != null) {
            booking.setTable(table);
        }
    }
}