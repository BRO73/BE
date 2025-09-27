package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Table;

import java.util.List;
import java.util.Optional;

public interface TableService {
    List<Table> getAllTables();
    Optional<Table> getTableById(Long id);
    Table createTable(Table table);
    Table updateTable(Long id, Table table);
    void deleteTable(Long id);
    Optional<Table> getTableByNumber(String tableNumber);
    List<Table> getTablesByStatus(String status);
    List<Table> getTablesByCapacity(Integer capacity);
    List<Table> getTablesByLocation(String location);
}
