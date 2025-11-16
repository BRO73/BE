package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.TableRequest;
import com.example.restaurant_management.dto.response.TableResponse;
import com.example.restaurant_management.entity.Booking;
import com.example.restaurant_management.entity.Location;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.repository.BookingRepository;
import com.example.restaurant_management.repository.LocationRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final LocationRepository locationRepository;
    private final BookingRepository bookingRepository;

    // ================================
    // ✅ GET TABLES BY DAY
    // ================================
    @Override
    public List<TableResponse> getTablesByDay(String date) {
        LocalDate day = LocalDate.parse(date);
        LocalDateTime startDay = day.atStartOfDay();
        LocalDateTime endDay = day.plusDays(1).atStartOfDay();

        // 1 query lấy tất cả booking trong ngày
        List<Booking> bookings = bookingRepository.findBookingsByDay(startDay, endDay);

        // Map<tableId, List<Booking>> – tránh N+1
        Map<Long, List<Booking>> bookingsByTable = new HashMap<>();
        for (Booking booking : bookings) {
            for (TableEntity table : booking.getTables()) {
                bookingsByTable.computeIfAbsent(table.getId(), k -> new ArrayList<>()).add(booking);
            }
        }

        // 1 query lấy tất cả table
        List<TableEntity> allTables = tableRepository.findAll();

        // Cập nhật status theo booking trong map
        allTables.forEach(table -> {
            List<Booking> tableBookings = bookingsByTable.getOrDefault(table.getId(), List.of());
            String status = tableBookings.stream()
                    .anyMatch(b -> "Confirmed".equalsIgnoreCase(b.getStatus())) ? "Reserved" :
                    tableBookings.stream().anyMatch(b -> "Pending".equalsIgnoreCase(b.getStatus())) ? "Occupied" :
                            "Available";
            table.setStatus(status);
        });

        return allTables.stream().map(TableResponse::fromEntity).toList();
    }

    // ================================
    // ✅ GET ALL TABLES
    // ================================
    @Override
    public List<TableResponse> getAllTables() {
        return tableRepository.findAllWithDetails().stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    // ================================
    // ✅ GET TABLE BY ID
    // ================================
    @Override
    public Optional<TableResponse> getTableById(Long id) {
        return tableRepository.findById(id).map(TableResponse::fromEntity);
    }

    // ================================
    // ✅ CREATE TABLE
    // ================================
    @Transactional
    @Override
    public TableResponse createTable(TableRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        TableEntity table = new TableEntity();
        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setStatus(request.getStatus() != null ? request.getStatus() : "Available");
        table.setLocation(location);

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    // ================================
    // ✅ UPDATE TABLE
    // ================================
    @Transactional
    @Override
    public TableResponse updateTable(Long id, TableRequest request) {
        TableEntity table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setLocation(location);
        table.setStatus(request.getStatus() != null ? request.getStatus() : table.getStatus());

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    // ================================
    // ✅ DELETE TABLE
    // ================================
    @Transactional
    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    // ================================
    // ✅ GET TABLE BY NUMBER
    // ================================
    @Override
    public Optional<TableResponse> getTableByNumber(String tableNumber) {
        return tableRepository.findByTableNumber(tableNumber)
                .map(TableResponse::fromEntity);
    }

    // ================================
    // ✅ GET TABLES BY STATUS
    // ================================
    @Override
    public List<TableResponse> getTablesByStatus(String status) {
        return tableRepository.findByStatus(status).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    // ================================
    // ✅ GET TABLES BY CAPACITY
    // ================================
    @Override
    public List<TableResponse> getTablesByCapacity(Integer capacity) {
        return tableRepository.findByCapacityGreaterThanEqual(capacity).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    // ================================
    // ✅ GET TABLES BY LOCATION
    // ================================
    @Override
    public List<TableResponse> getTablesByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));

        return tableRepository.findByLocation(location).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    // ================================
    // ✅ GET TABLES BY BOOKING
    // ================================
    @Override
    public List<TableResponse> getTablesByBooking(Booking booking) {
        return booking.getTables().stream()
                .map(TableResponse::fromEntity)
                .toList();
    }
}
