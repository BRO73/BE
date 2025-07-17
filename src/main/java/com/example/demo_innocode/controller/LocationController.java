package com.example.demo_innocode.controller;

import com.example.demo_innocode.dto.request.LocationRequestDTO;
import com.example.demo_innocode.dto.response.LocationResponseDTO;
import com.example.demo_innocode.entity.Location;
import com.example.demo_innocode.entity.Media;
import com.example.demo_innocode.service.impl.LocationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor

@CrossOrigin(origins = "http://localhost:8080")
public class LocationController {

    private final LocationServiceImpl locationService;
    private final LocationServiceImpl locationServiceImpl;

    @GetMapping
    public ResponseEntity<List<LocationResponseDTO>> getAllLocations() {
        List<LocationResponseDTO> locations = locationService.getAllLocations();
        return new ResponseEntity<>(locations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Location> getLocationById(@PathVariable Long id) {
        Optional<Location> location = locationService.getLocationById(id);
        return location.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/media")
    public ResponseEntity<List<Media>> getMediaByLocationId(@PathVariable Long id) {
        List<Media> mediaList = locationService.getMediaByLocationId(id);
        return new ResponseEntity<>(mediaList, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Location> addLocation(@RequestBody List<LocationRequestDTO> locationRequestDTOs) {
        locationServiceImpl.addLocations(locationRequestDTOs);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
