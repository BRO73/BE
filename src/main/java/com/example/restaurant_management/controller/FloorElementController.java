package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.FloorElementRequest;
import com.example.restaurant_management.dto.response.FloorElementResponse;
import com.example.restaurant_management.service.FloorElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/api/elements")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FloorElementController {
private final FloorElementService service;


@GetMapping
public ResponseEntity<List<FloorElementResponse>> getAll() {
return ResponseEntity.ok(service.getAll());
}


@GetMapping("/{id}")
public ResponseEntity<FloorElementResponse> getById(@PathVariable String id) {
return ResponseEntity.ok(service.getById(id));
}


@PostMapping
public ResponseEntity<FloorElementResponse> create(@RequestBody FloorElementRequest request) {
return ResponseEntity.ok(service.create(request));
}


@PutMapping("/{id}")
public ResponseEntity<FloorElementResponse> update(@PathVariable String id, @RequestBody FloorElementRequest request) {
return ResponseEntity.ok(service.update(id, request));
}


@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable String id) {
service.delete(id);
return ResponseEntity.noContent().build();
}
}