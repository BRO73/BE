package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Table;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.service.TableService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;

    public TableServiceImpl(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @Override
    public List<Table> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public Optional<Table> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    @Override
    public Table createTable(Table table) {
        return tableRepository.save(table);
    }

    @Override
    public Table updateTable(Long id, Table table) {
        table.setId(id);
        return tableRepository.save(table);
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public Optional<Table> getTableByNumber(String tableNumber) {
        return tableRepository.findByTableNumber(tableNumber);
    }

    @Override
    public List<Table> getTablesByStatus(String status) {
        return tableRepository.findByStatus(status);
    }

    @Override
    public List<Table> getTablesByCapacity(Integer capacity) {
        return tableRepository.findByCapacityGreaterThanEqual(capacity);
    }

    @Override
    public List<Table> getTablesByLocation(String location) {
        return tableRepository.findByLocation(location);
    }
}
