package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.TableEntity;

import java.util.List;
import java.util.Optional;

public interface TableService {
    List<TableEntity> getAllTables();
    Optional<TableEntity> getTableById(Long id);
    TableEntity createTable(TableEntity tableEntity);
    TableEntity updateTable(Long id, TableEntity tableEntity);
    void deleteTable(Long id);
    Optional<TableEntity> getTableByNumber(String tableNumber);
    List<TableEntity> getTablesByStatus(String status);
    List<TableEntity> getTablesByCapacity(Integer capacity);
    List<TableEntity> getTablesByLocation(String location);
}
