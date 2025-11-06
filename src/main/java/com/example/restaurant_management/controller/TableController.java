package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.TableRequest;
import com.example.restaurant_management.dto.response.TableResponse;
import com.example.restaurant_management.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
//@PreAuthorize("hasAnyRole('ADMIN','WAITSTAFF')")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public ResponseEntity<List<TableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableResponse> getTableById(@PathVariable Long id) {
        return tableService.getTableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TableResponse> createTable(@RequestBody TableRequest request) {
        return ResponseEntity.ok(tableService.createTable(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableResponse> updateTable(@PathVariable Long id, @RequestBody TableRequest request) {
        return ResponseEntity.ok(tableService.updateTable(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/number/{tableNumber}")
    public ResponseEntity<TableResponse> getTableByNumber(@PathVariable String tableNumber) {
        return tableService.getTableByNumber(tableNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TableResponse>> getTablesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(tableService.getTablesByStatus(status));
    }


    @GetMapping("/capacity/{capacity}")
    public ResponseEntity<List<TableResponse>> getTablesByCapacity(@PathVariable Integer capacity) {
        return ResponseEntity.ok(tableService.getTablesByCapacity(capacity));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<TableResponse>> getTablesByLocation(@PathVariable Long locationId) {
        return ResponseEntity.ok(tableService.getTablesByLocation(locationId));
    }
    @GetMapping("/day/{date}")
    public ResponseEntity<List<TableResponse>> getTablesByDay(@PathVariable String date) {
        return ResponseEntity.ok(tableService.getTablesByDay(date));
    }

}
