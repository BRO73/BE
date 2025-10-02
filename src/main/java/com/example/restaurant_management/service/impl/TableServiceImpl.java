package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.TableRequest;
import com.example.restaurant_management.dto.response.TableResponse;
import com.example.restaurant_management.entity.Location;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.repository.LocationRepository;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.service.TableService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final LocationRepository locationRepository;

    @Override
    public List<TableResponse> getAllTables() {
        return tableRepository.findAll().stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    @Override
    public Optional<TableResponse> getTableById(Long id) {
        return tableRepository.findById(id).map(TableResponse::fromEntity);
    }

    @Override
    public TableResponse createTable(TableRequest request) {
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        TableEntity table = new TableEntity();
        table.setTableNumber(request.getTableNumber());
        table.setCapacity(request.getCapacity());
        table.setLocation(location);
        table.setStatus(request.getStatus());

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

        return TableResponse.fromEntity(tableRepository.save(table));
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public Optional<TableResponse> getTableByNumber(String tableNumber) {
        return tableRepository.findByTableNumber(tableNumber).map(TableResponse::fromEntity);
    }

    @Override
    public List<TableResponse> getTablesByStatus(String status) {
        return tableRepository.findByStatus(status).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TableResponse> getTablesByCapacity(Integer capacity) {
        return tableRepository.findByCapacityGreaterThanEqual(capacity).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }

    @Override
    public List<TableResponse> getTablesByLocation(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new RuntimeException("Location not found"));
        return tableRepository.findByLocation(location).stream()
                .map(TableResponse::fromEntity)
                .toList();
    }
}
