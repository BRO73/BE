package com.example.demo_innocode.controller;

import com.example.demo_innocode.dto.request.ItineraryRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryResponseDTO;
import com.example.demo_innocode.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary")
@RequiredArgsConstructor
public class ItineraryController {
    private final ItineraryService itineraryService;

    // Lấy danh sách hành trình theo userId
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<ItineraryResponseDTO>> getItinerariesByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(itineraryService.getItinerariesByUserId(userId));
    }

    // Tạo mới itinerary (dùng để insert vào db)
    @PostMapping("/add")
    public ResponseEntity<ItineraryResponseDTO> createItinerary(@RequestBody ItineraryRequestDTO dto) {
        return ResponseEntity.ok(itineraryService.createItinerary(dto));
    }
}
