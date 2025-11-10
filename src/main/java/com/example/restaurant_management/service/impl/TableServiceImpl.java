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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final LocationRepository locationRepository;
    private final BookingRepository bookingRepository;
    @Override
    public List<TableResponse> getTablesByDay(String date) {
        LocalDateTime targetDay = LocalDateTime.parse(date + "T00:00:00");

        List<TableEntity> allTables = tableRepository.findAll();

        for (TableEntity table : allTables) {
            List<Booking> bookingsOnDay = bookingRepository.findByTableAndDay(table.getId(), targetDay);
            String status = "Available";

            for (Booking b : bookingsOnDay) {
                if ("Confirmed".equalsIgnoreCase(b.getStatus())) {
                    status = "Reserved";
                    break;
                } else if ("Pending".equalsIgnoreCase(b.getStatus())) {
                    status = "Occupied";
                }
            }

            table.setStatus(status);
            tableRepository.save(table);
        }

        return allTables.stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    // ======================================================
    // ✅ HELPER: cập nhật trạng thái tất cả bàn hôm nay
    // ======================================================
    public void updateAllTablesStatusToday() {
        LocalDateTime today = LocalDateTime.now();
        List<TableEntity> allTables = tableRepository.findAll();

        for (TableEntity table : allTables) {
            List<Booking> bookingsToday = bookingRepository.findByTableAndDay(table.getId(), today);
            String status = "Available";

            for (Booking b : bookingsToday) {
                if ("Confirmed".equalsIgnoreCase(b.getStatus())) {
                    status = "Reserved";
                    break;
                } else if ("Pending".equalsIgnoreCase(b.getStatus())) {
                    status = "Occupied";
                }
            }

            table.setStatus(status);
            tableRepository.save(table);
        }
    }


    @Override
    public List<TableResponse> getAllTables() {
        updateAllTablesStatusToday();
        return tableRepository.findAll().stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    @Override
    public Optional<TableResponse> getTableById(Long id) {
        updateAllTablesStatusToday();
        return tableRepository.findById(id).map(TableResponse::fromEntity);
    }

    @Override
    public TableResponse createTable(TableRequest request) {

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        TableEntity table = new TableEntity();
        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setStatus(request.getStatus());
        table.setLocation(location);

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    @Override
    public TableResponse updateTable(Long id, TableRequest request) {
        TableEntity table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setLocation(location);
        table.setStatus(request.getStatus());
        updateAllTablesStatusToday();

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public Optional<TableResponse> getTableByNumber(String tableNumber) {
        updateAllTablesStatusToday();

        return tableRepository.findByTableNumber(tableNumber).map(TableResponse::fromEntity);

    }

    @Override
    public List<TableResponse> getTablesByStatus(String status) {
        updateAllTablesStatusToday();

        return tableRepository.findByStatus(status).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TableResponse> getTablesByCapacity(Integer capacity) {
        updateAllTablesStatusToday();
        return tableRepository.findByCapacityGreaterThanEqual(capacity).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TableResponse> getTablesByLocation(Long locationId) {

        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        updateAllTablesStatusToday();

        return tableRepository.findByLocation(location).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TableResponse> getTablesByBooking(Booking booking) {
        List<Long> tableIds = booking.getTables().stream().map(tb -> tb.getId()).toList();
        List<TableEntity> tbs = new ArrayList<>();
        for (Long tableId : tableIds) {
            tableRepository.findById(tableId).ifPresent(tbs::add);
        }
        return tbs.stream()
                .map(TableResponse::fromEntity)
                .toList();
    }
}
