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
import com.example.restaurant_management.util.EmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;

    // ======================================================
    // ✅ HELPER: cập nhật trạng thái bàn theo tất cả booking trong ngày
    // ======================================================
    private void updateTableStatusByDay(
            TableEntity table,
            Map<Long, List<Booking>> bookingsMap
    ) {
        List<Booking> bookingsToday =
                bookingsMap.getOrDefault(table.getId(), List.of());

        String status = "Available";

        for (Booking b : bookingsToday) {
            if ("Confirmed".equalsIgnoreCase(b.getStatus())) {
                status = "Reserved";
                break;
            } else if ("Pending".equalsIgnoreCase(b.getStatus())) {
                status = "Occupied";
            }
        }

        table.setStatus(status); // ❗ Không save ở đây nữa
    }


    // ======================================================
    // ✅ CREATE BOOKING
    // ======================================================
    @Override
    public BookingResponse createBooking(BookingRequest request) {
        validateBookingRequest(request); // ✅ thêm để tránh NPE

        List<TableEntity> tables = new ArrayList<>();
        for (Long tbId : request.getTableIds()) {
            TableEntity table = tableRepository.findById(tbId)
                    .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + tbId));
            List<Booking> bookingsToday = bookingRepository.findByTableAndDay(table.getId(), request.getBookingTime());
            if (!bookingsToday.isEmpty()) {
                throw new IllegalStateException("Table " + table.getTableNumber() + " already has a booking on this day!");
            }
            tables.add(table);
        }

        Customer customer = customerRepository.findByPhoneNumber(request.getCustomerPhone())
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .fullName(request.getCustomerName())
                            .phoneNumber(request.getCustomerPhone())
                            .email(request.getCustomerEmail()) // optional
                            .build();
                    return customerRepository.save(newCustomer);
                });

        User customerUser = customer.getUser();
        Booking booking = bookingMapper.toEntity(request, tables, customerUser);
        booking.setStatus(request.getStatus() != null ? request.getStatus() : "Pending");

        Booking saved = bookingRepository.save(booking);

        // ✅ Optional: gửi email khi tạo mới
        if (customer.getEmail() != null && !customer.getEmail().isBlank()) {
            emailService.sendBookingConfirmation(
                    customer.getEmail(),
                    customer.getFullName(),
                    request.getBookingTime().toLocalDate().toString(),
                    request.getBookingTime().toLocalTime().toString()
            );
        }
        updateAllTableStatuses(request.getBookingTime());
        return bookingMapper.toResponse(saved);
    }


    // ======================================================
    // ✅ UPDATE BOOKING
    // ======================================================
    @Override
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        String oldStatus = existing.getStatus(); // 🔹 Lưu trạng thái cũ

        validateBookingRequest(request);

        List<TableEntity> newTables = new ArrayList<>();
        for (Long tbId : request.getTableIds()) {
            TableEntity table = tableRepository.findById(tbId)
                    .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + tbId));
            // Check booking trùng trong ngày, trừ chính booking này
            List<Booking> bookingsToday = bookingRepository.findByTableAndDay(table.getId(), request.getBookingTime());
            boolean conflict = bookingsToday.stream().anyMatch(b -> !b.getId().equals(id));
            if (conflict) {
                throw new IllegalStateException("Table " + table.getTableNumber() + " already has a booking on this day!");
            }
            newTables.add(table);
        }

        // Map dữ liệu mới vào entity
        bookingMapper.updateEntityFromRequest(existing, request, newTables);
        Booking updated = bookingRepository.save(existing);

        // 🔹 Gửi email nếu trạng thái chuyển từ Pending → Confirmed
        if ("Pending".equalsIgnoreCase(oldStatus) && "Confirmed".equalsIgnoreCase(updated.getStatus())) {
            String toEmail = updated.getCustomerName()!= null ? updated.getCustomerEmail(): null;

            if (toEmail != null && !toEmail.isBlank()) {
                emailService.sendBookingConfirmation(
                        toEmail,
                        updated.getCustomerName(),
                        updated.getBookingTime().toLocalDate().toString(),
                        updated.getBookingTime().toLocalTime().toString()
                );
            } else {
                System.out.println("⚠️ Không thể gửi email vì khách hàng chưa có email.");
            }
        }

        updateAllTableStatuses(request.getBookingTime());
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

        bookingRepository.delete(existing);

        updateAllTableStatuses(existing.getBookingTime());

    }

    // ======================================================
    // ✅ UPDATE STATUS
    // ======================================================
    @Override
    public BookingResponse updateStatus(Long id, String status) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        String oldStatus = existing.getStatus(); // 🔹 Lưu lại trạng thái cũ

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

        // 🔹 Nếu chuyển từ Pending -> Confirmed thì gửi email xác nhận
        if ("Pending".equalsIgnoreCase(oldStatus) && "Confirmed".equalsIgnoreCase(status)) {
            String toEmail = existing.getCustomerEmail() != null ? existing.getCustomerEmail() : null;

            if (toEmail != null && !toEmail.isBlank()) {
                emailService.sendBookingConfirmation(
                        toEmail,
                        existing.getCustomerName(),
                        existing.getBookingTime().toLocalDate().toString(),
                        existing.getBookingTime().toLocalTime().toString()
                );
            } else {
                System.out.println("⚠️ Không thể gửi email vì khách hàng chưa có email.");
            }
        }

        updateAllTableStatuses(existing.getBookingTime());


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

        List<Booking> bookingsToday = bookingRepository.findByTableAndDay(tableId, bookingTime);
        boolean available = bookingsToday.isEmpty();

        return TableAvailabilityResponse.builder()
                .tableId(tableId)
                .available(available)
                .message(available ? "Table is available" : "Table already has a booking today")
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
        List<Booking> bookings = bookingRepository.findByCustomerUser(userRepository.findByCustomer(customerRepository.findById(customerUserId).get()).get());
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
    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByCustomerId(Long customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerUser(userRepository.findByCustomer(customerRepository.findById(customerId).get()).get());
        return bookingMapper.toResponseList(bookings);
    }

    private Map<Long, List<Booking>> getBookingsGroupedByTable(LocalDateTime bookingTime) {

        LocalDateTime startDay = bookingTime.toLocalDate().atStartOfDay();
        LocalDateTime endDay = startDay.plusDays(1);

        List<Booking> bookings = bookingRepository.findBookingsWithTablesByDay(startDay, endDay);

        Map<Long, List<Booking>> map = new HashMap<>();

        for (Booking b : bookings) {
            for (TableEntity t : b.getTables()) {
                map.computeIfAbsent(t.getId(), x -> new ArrayList<>()).add(b);
            }
        }

        return map;
    }

    private void updateAllTableStatuses(LocalDateTime bookingTime) {

        // 1 query lấy toàn bộ bookings trong ngày
        Map<Long, List<Booking>> bookingsMap = getBookingsGroupedByTable(bookingTime);

        // Lấy tất cả bàn (1 query)
        List<TableEntity> allTables = tableRepository.findAll();

        for (TableEntity table : allTables) {
            updateTableStatusByDay(table, bookingsMap);
        }

        // Save tất cả cùng lúc (1 query)
        tableRepository.saveAll(allTables);
    }


}