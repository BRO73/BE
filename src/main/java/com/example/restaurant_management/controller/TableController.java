package com.example.restaurant_management.controller;

import com.example.restaurant_management.entity.Table;
import com.example.restaurant_management.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<Table>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Table> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Table> createTable(@RequestBody Table table) {
        return ResponseEntity.ok(tableService.createTable(table));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Table> updateTable(@PathVariable Long id, @RequestBody Table table) {
        return ResponseEntity.ok(tableService.updateTable(id, table));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/number/{tableNumber}")
    public ResponseEntity<Table> getTableByNumber(@PathVariable String tableNumber) {
        return tableService.getTableByNumber(tableNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Table>> getTablesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(tableService.getTablesByStatus(status));
    }

    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<Table>> getTablesByCapacity(@PathVariable Integer capacity) {
        return ResponseEntity.ok(tableService.getTablesByCapacity(capacity));
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Table>> getTablesByLocation(@PathVariable String location) {
        return ResponseEntity.ok(tableService.getTablesByLocation(location));
    }
}
