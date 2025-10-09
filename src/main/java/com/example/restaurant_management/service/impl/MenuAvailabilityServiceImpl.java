package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.MenuItem;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.service.MenuAvailabilityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuAvailabilityServiceImpl implements MenuAvailabilityService {

    private final MenuItemRepository repo;

    public MenuAvailabilityServiceImpl(MenuItemRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuItem> listAll() {
        return repo.findAll();
    }

    @Override
    @Transactional
    public boolean setAvailability(Long id, boolean available) {
        MenuItem item = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found: " + id));
        String next = available ? "available" : "unavailable";
        item.setStatus(next);
        repo.save(item);
        return available;
    }
}
