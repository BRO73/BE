package com.example.demo_innocode.controller;

import com.example.demo_innocode.dto.request.ItineraryStopRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryStopResponseDTO;
import com.example.demo_innocode.service.ItineraryStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/itinerary-stop")
@RequiredArgsConstructor
public class ItineraryStopController {

    private final ItineraryStopService itineraryStopService;

    @PostMapping()
    public ResponseEntity<ItineraryStopResponseDTO> addItineraryStop(@RequestBody ItineraryStopRequestDTO dto) {
        return ResponseEntity.ok(itineraryStopService.addItineraryStop(dto));
    }

    // Cập nhật thông tin một điểm dừng
    @PutMapping("/{id}")
    public ResponseEntity<ItineraryStopResponseDTO> updateItineraryStop(@PathVariable Long id, @RequestBody ItineraryStopRequestDTO dto) {
        return ResponseEntity.ok(itineraryStopService.updateItineraryStop(id, dto));
    }

    // Xóa một điểm dừng khỏi hành trình
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItineraryStop(@PathVariable Long id) {
        itineraryStopService.deleteItineraryStop(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ItineraryStopResponseDTO>> getAllItineraryStops() {
        return ResponseEntity.ok(itineraryStopService.getAllItineraryStops());
    }

    @GetMapping("/by-itinerary/{itineraryId}")
    public ResponseEntity<List<ItineraryStopResponseDTO>> getStopsByItineraryId(@PathVariable Long itineraryId) {
        return ResponseEntity.ok(itineraryStopService.getStopsByItineraryId(itineraryId));
    }

}
