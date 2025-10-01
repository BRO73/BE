package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.TableEntity;
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
    public List<TableEntity> getAllTables() {
        return tableRepository.findAll();
    }

    @Override
    public Optional<TableEntity> getTableById(Long id) {
        return tableRepository.findById(id);
    }

    @Override
    public TableEntity createTable(TableEntity tableEntity) {
        return tableRepository.save(tableEntity);
    }

    @Override
    public TableEntity updateTable(Long id, TableEntity tableEntity) {
        tableEntity.setId(id);
        return tableRepository.save(tableEntity);
    }

    @Override
    public void deleteTable(Long id) {
        tableRepository.deleteById(id);
    }

    @Override
    public Optional<TableEntity> getTableByNumber(String tableNumber) {
        return tableRepository.findByTableNumber(tableNumber);
    }

    @Override
    public List<TableEntity> getTablesByStatus(String status) {
        return tableRepository.findByStatus(status);
    }

    @Override
    public List<TableEntity> getTablesByCapacity(Integer capacity) {
        return tableRepository.findByCapacityGreaterThanEqual(capacity);
    }

    @Override
    public List<TableEntity> getTablesByLocation(String location) {
        return tableRepository.findByLocation(location);
    }
}
