package com.example.restaurant_management.controller;

import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@PreAuthorize("hasAnyRole('ADMIN','WAITSTAFF')")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<TableEntity>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableEntity> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TableEntity> createTable(@RequestBody TableEntity tableEntity) {
        return ResponseEntity.ok(tableService.createTable(tableEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableEntity> updateTable(@PathVariable Long id, @RequestBody TableEntity tableEntity) {
        return ResponseEntity.ok(tableService.updateTable(id, tableEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/number/{tableNumber}")
    public ResponseEntity<TableEntity> getTableByNumber(@PathVariable String tableNumber) {
        return tableService.getTableByNumber(tableNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TableEntity>> getTablesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(tableService.getTablesByStatus(status));
    }

    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<TableEntity>> getTablesByCapacity(@PathVariable Integer capacity) {
        return ResponseEntity.ok(tableService.getTablesByCapacity(capacity));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<TableEntity>> getTablesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(tableService.getTablesByLocation(location));
    }
}
