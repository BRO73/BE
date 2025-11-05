package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.FloorElementRequest;
import com.example.restaurant_management.dto.response.FloorElementResponse;
import java.util.List;


public interface FloorElementService {
List<FloorElementResponse> getAll();
FloorElementResponse getById(String id);
FloorElementResponse create(FloorElementRequest request);
FloorElementResponse update(String id, FloorElementRequest request);
void delete(String id);
}