package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.MenuItem;
import java.util.List;

public interface MenuAvailabilityService {
    List<MenuItem> listAll();
    boolean setAvailability(Long id, boolean available);
}
