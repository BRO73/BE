package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.BookingRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.TableAvailabilityResponse;
import com.example.restaurant_management.entity.Booking;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.mapper.BookingMapper;
import com.example.restaurant_management.repository.BookingRepository;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final CustomerRepository customerRepository;


    @Override
    public BookingResponse createBooking(BookingRequest request) {
        // ✅ 1. Lấy danh sách bàn
        List<TableEntity> tables = new ArrayList<>();
        for(TableEntity tableEntity : tableRepository.findAll()){
            for(Long tbId : request.getTableIds()){
                if(tbId == tableEntity.getId()) tables.add(tableEntity);
            }
        }
        if (tables.isEmpty()) {
            throw new EntityNotFoundException("No tables found for provided IDs");
        }

        // ✅ 2. Kiểm tra bàn có trống không
        for (TableEntity table : tables) {
            if (!"Available".equalsIgnoreCase(table.getStatus())) {
                throw new IllegalStateException("Table " + table.getTableNumber() + " is currently not available!");
            }

            List<Booking> existing = bookingRepository.findByTableAndDay(table.getId(), request.getBookingTime());
            if (!existing.isEmpty()) {
                throw new IllegalStateException("Table " + table.getTableNumber() + " already has a booking on this day!");
            }
        }

        // ✅ 3. Tìm hoặc tạo Customer qua số điện thoại
        Customer customer = customerRepository.findByPhoneNumber(request.getCustomerPhone())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .fullName(request.getCustomerName())
                            .phoneNumber(request.getCustomerPhone())
                            .email(null)
                            .build();
                    return customerRepository.save(newCustomer);
                });

        // ✅ 4. Lấy User gắn với Customer (nếu có)
        User customerUser = customer.getUser();

        // ✅ 5. Map sang entity
        Booking booking = bookingMapper.toEntity(request, tables, customerUser);
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "Pending");

        Booking saved = bookingRepository.save(booking);

        // ✅ 6. Cập nhật trạng thái bàn
        for (TableEntity table : tables) {
            table.setStatus("Occupied");
            tableRepository.save(table);
        }

        return bookingMapper.toResponse(saved);
    }

    // ======================================================
    // ✅ UPDATE BOOKING
    // ======================================================
    @Override
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        validateBookingRequest(request);

        List<TableEntity> newTables = tableRepository.findAllById(request.getTableIds());
        if (newTables.isEmpty()) {
            throw new EntityNotFoundException("No tables found for the provided IDs");
        }

        // 🔹 Kiểm tra xung đột và tình trạng bàn
        for (TableEntity table : newTables) {
            boolean isCurrentTable = existing.getTables().contains(table);

            if (!isCurrentTable && !"Available".equalsIgnoreCase(table.getStatus())) {
                throw new IllegalStateException("Table " + table.getTableNumber() + " is currently not available!");
            }

            List<Booking> conflicts = bookingRepository.findByTableAndDay(table.getId(), request.getBookingTime());
            if (!conflicts.isEmpty() && !conflicts.get(0).getId().equals(id)) {
                throw new IllegalStateException("Table " + table.getTableNumber() + " already has a booking on that day");
            }
        }

        // 🔹 Map dữ liệu mới từ request vào entity
        bookingMapper.updateEntityFromRequest(existing, request, newTables);
        Booking updated = bookingRepository.save(existing);

        // 🔹 Xử lý trạng thái bàn cũ không còn trong booking -> Available
        for (TableEntity oldTable : existing.getTables()) {
            if (!newTables.contains(oldTable)) {
                oldTable.setStatus("Available");
                tableRepository.save(oldTable);
            }
        }

        // 🔹 Cập nhật trạng thái bàn mới theo trạng thái booking
        String newStatus = request.getStatus();
        for (TableEntity table : newTables) {
            if ("Confirmed".equalsIgnoreCase(newStatus)) {
                table.setStatus("Reserved");
            } else if ("Cancelled".equalsIgnoreCase(newStatus) || "Completed".equalsIgnoreCase(newStatus)) {
                table.setStatus("Available");
            } else {
                table.setStatus("Occupied");
            }
            tableRepository.save(table);
        }

        return bookingMapper.toResponse(updated);
    }


    // ======================================================
    // ✅ DELETE BOOKING
    // ======================================================
    @Override
    public void deleteBooking(Long id) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        if ("Completed".equalsIgnoreCase(existing.getStatus())) {
            throw new IllegalStateException("Cannot delete a completed booking");
        }

        // Trả bàn về Available
        for (TableEntity table : existing.getTables()) {
            table.setStatus("Available");
            tableRepository.save(table);
        }

        bookingRepository.delete(existing);
    }

    // ======================================================
    // ✅ UPDATE STATUS
    // ======================================================
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

        // Nếu bị Cancelled hoặc Completed → bàn Available
        if ("Cancelled".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
            for (TableEntity table : existing.getTables()) {
                table.setStatus("Available");
                tableRepository.save(table);
            }
        }

        return bookingMapper.toResponse(updated);
    }

    // ======================================================
    // ✅ CHECK TABLE AVAILABILITY
    // ======================================================
    @Override
    @Transactional(readOnly = true)
    public TableAvailabilityResponse isTableAvailable(Long tableId, LocalDateTime bookingTime) {
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table not found"));

        List<Booking> sameDayBookings = bookingRepository.findByTableAndDay(tableId, bookingTime);
        boolean available = sameDayBookings.isEmpty() && "Available".equalsIgnoreCase(table.getStatus());

        return TableAvailabilityResponse.builder()
                .tableId(tableId)
                .available(available)
                .message(available ? "Table is available" : "Table is already booked or occupied")
                .build();
    }

    // ======================================================
    // ✅ GETTERS
    // ======================================================
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

    // ======================================================
    // ✅ PRIVATE VALIDATOR
    // ======================================================
    private void validateBookingRequest(BookingRequest request) {
        if (request.getCustomerName() == null || request.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (request.getCustomerPhone() == null || !request.getCustomerPhone().matches("\\d{9,15}")) {
            throw new IllegalArgumentException("Invalid phone number");
        }
        if (request.getBookingTime() == null) {
            throw new IllegalArgumentException("Booking time is required");
        }
        if (request.getTableIds() == null || request.getTableIds().isEmpty()) {
            throw new IllegalArgumentException("At least one table must be selected");
        }
    }
}
