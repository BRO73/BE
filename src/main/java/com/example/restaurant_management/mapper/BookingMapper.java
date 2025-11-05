package com.example.restaurant_management.mapper;

import com.example.restaurant_management.dto.request.BookingRequest;
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

    // ✅ Request → Entity
    public Booking toEntity(BookingRequest request, List<TableEntity> tables, User customerUser) {
        Booking booking = new Booking();
        booking.setCustomerName(request.getCustomerName());
        booking.setCustomerPhone(request.getCustomerPhone());
        booking.setBookingTime(request.getBookingTime());
        booking.setNumGuests(request.getNumGuests());
        booking.setNotes(request.getNotes());
        booking.setTables(tables);
        booking.setCustomerUser(customerUser);
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "Pending");
        booking.setCustomerEmail(request.getCustomerEmail());
        return booking;
    }

    // ✅ Update Entity
    public void updateEntityFromRequest(Booking booking, BookingRequest request, List<TableEntity> tables) {
        if (request.getCustomerName() != null) booking.setCustomerName(request.getCustomerName());
        if (request.getCustomerPhone() != null) booking.setCustomerPhone(request.getCustomerPhone());
        if (request.getBookingTime() != null) booking.setBookingTime(request.getBookingTime());
        if (request.getNumGuests() != null) booking.setNumGuests(request.getNumGuests());
        if (request.getNotes() != null) booking.setNotes(request.getNotes());
        if (tables != null && !tables.isEmpty()) booking.setTables(tables);
        if (request.getStatus() != null) booking.setStatus(request.getStatus());
        if(request.getCustomerEmail() != null) booking.setCustomerEmail(request.getCustomerEmail());
    }

    // ✅ Entity → Response
    public BookingResponse toResponse(Booking booking) {
        List<TableSimpleResponse> tableResponses = booking.getTables() != null
                ? booking.getTables().stream().map(this::toTableSimpleResponse).collect(Collectors.toList())
                : List.of();

        CustomerSimpleResponse customerResponse = booking.getCustomerUser() != null
                ? toCustomerSimpleResponse(booking.getCustomerUser())
                : null;

        return BookingResponse.builder()
                .id(booking.getId())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .bookingTime(booking.getBookingTime())
                .numGuests(booking.getNumGuests())
                .notes(booking.getNotes())
                .status(booking.getStatus())
                .table(tableResponses)
                .customer(customerResponse)
                .createdAt(booking.getCreatedAt())
                .updatedAt(booking.getUpdatedAt())
                .customerEmail(booking.getCustomerEmail())
                .build();
    }

    public List<BookingResponse> toResponseList(List<Booking> bookings) {
        return bookings.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private TableSimpleResponse toTableSimpleResponse(TableEntity table) {
        return TableSimpleResponse.builder()
                .id(table.getId())
                .capacity(table.getCapacity())
                .tableNumber(table.getTableNumber())
                .status(table.getStatus())
                .build();
    }

    private CustomerSimpleResponse toCustomerSimpleResponse(User user) {
        return CustomerSimpleResponse.builder()
                .id(user.getId())
                .build();
    }
}
