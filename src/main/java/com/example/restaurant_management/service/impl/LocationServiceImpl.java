package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.LocationRequest;
import com.example.restaurant_management.dto.response.LocationResponse;
import com.example.restaurant_management.entity.Location;
import com.example.restaurant_management.repository.LocationRepository;
import com.example.restaurant_management.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    public LocationResponse create(LocationRequest request) {
        if (locationRepository.existsByName(request.getName())) {
            throw new RuntimeException("Location already exists with name: " + request.getName());
        }
        Location location = Location.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Location saved = locationRepository.save(location);
        return mapToResponse(saved);
    }

    @Override
    public LocationResponse update(Long id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));

        location.setName(request.getName());
        location.setDescription(request.getDescription());

        Location updated = locationRepository.save(location);
        return mapToResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        locationRepository.delete(location);
    }

    @Override
    public LocationResponse getById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return mapToResponse(location);
    }

    @Override
    public List<LocationResponse> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private LocationResponse mapToResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .description(location.getDescription())
                .build();
    }
}
