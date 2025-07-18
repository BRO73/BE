package com.example.demo_innocode.controller;

import com.example.demo_innocode.dto.request.ItineraryStopRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryStopResponseDTO;
import com.example.demo_innocode.service.ItineraryStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itinerary-stop")
@RequiredArgsConstructor
public class ItineraryStopController {

    private final ItineraryStopService itineraryStopService;

    @PostMapping("/add")
    public ResponseEntity<ItineraryStopResponseDTO> addItineraryStop(@RequestBody ItineraryStopRequestDTO dto) {
        return ResponseEntity.ok(itineraryStopService.addItineraryStop(dto));
    }
}
