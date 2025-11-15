// src/main/java/com/example/restaurant_management/controller/TableTokenController.java
package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.response.TableResponse;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.repository.TableRepository;
import com.example.restaurant_management.service.TableTokenService;
import com.example.restaurant_management.service.impl.TableServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableTokenController {

    private final TableRepository tableRepository;
    private final TableTokenService tableTokenService;
    private final TableServiceImpl tableServiceImpl;

    @GetMapping("/resolve/{token}")
    public ResponseEntity<Optional<TableResponse>> resolve(@PathVariable String token) {
        Long tableId = tableTokenService.resolveTableId(token);
        // Tận dụng service sẵn có của bạn để trả DTO chuẩn
        return ResponseEntity.ok(tableServiceImpl.getTableById(tableId));
    }

    @GetMapping("/{id}/token")
    public ResponseEntity<TokenDTO> token(@PathVariable Long id) {
        tableRepository.findById(id).orElseThrow(() -> new RuntimeException("Table not found"));
        String token = tableTokenService.hashTableId(id);
        return ResponseEntity.ok(new TokenDTO(token));
    }

    public record TokenDTO(String token) {}
}
