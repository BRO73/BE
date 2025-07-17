package com.example.demo_innocode.service.impl; // Thay đổi package này

import com.example.demo_innocode.entity.VirtualTour;
import com.example.demo_innocode.repository.VirtualTourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VirtualTourServiceImpl {
    private final VirtualTourRepository virtualTourRepository;

    public Optional<VirtualTour> getVirtualTourById(Long id) {
        return virtualTourRepository.findById(id);
    }
}
