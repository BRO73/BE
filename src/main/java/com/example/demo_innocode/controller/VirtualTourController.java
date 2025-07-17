package com.example.demo_innocode.controller; // Thay đổi package này

import com.example.demo_innocode.entity.VirtualTour;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.demo_innocode.service.VirtualTourServiceImpl;

import java.util.Optional;

@RestController
@RequestMapping("/api/virtual-tours")
@RequiredArgsConstructor
public class VirtualTourController {
    private final VirtualTourServiceImpl virtualTourServiceImpl;

    @GetMapping("/{id}")
    public ResponseEntity<VirtualTour> getVirtualTourById(@PathVariable Long id) {
        Optional<VirtualTour> virtualTour = virtualTourServiceImpl.getVirtualTourById(id);
        return virtualTour.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
