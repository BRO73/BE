package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.TableRequest;
import com.example.restaurant_management.dto.response.TableResponse;

import java.util.List;
import java.util.Optional;

public interface TableService {
    List<TableResponse> getAllTables();
    Optional<TableResponse> getTableById(Long id);
    TableResponse createTable(TableRequest request);
    TableResponse updateTable(Long id, TableRequest request);
    void deleteTable(Long id);
    Optional<TableResponse> getTableByNumber(String tableNumber);
    List<TableResponse> getTablesByStatus(String status);
    List<TableResponse> getTablesByCapacity(Integer capacity);
    List<TableResponse> getTablesByLocation(Long locationId);
}
