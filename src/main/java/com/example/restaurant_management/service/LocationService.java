package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.LocationRequest;
import com.example.restaurant_management.dto.response.LocationResponse;

import java.util.List;

public interface LocationService {
    LocationResponse create(LocationRequest request);
    LocationResponse update(Long id, LocationRequest request);
    void delete(Long id);
    LocationResponse getById(Long id);
    List<LocationResponse> getAll();
}
