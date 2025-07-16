package com.example.demo_innocode.service; // Thay đổi package này

import com.example.demo_innocode.entity.Location;
import com.example.demo_innocode.entity.Media;
import com.example.demo_innocode.repository.LocationRepository;
import com.example.demo_innocode.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    private final MediaRepository mediaRepository;

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public List<Media> getMediaByLocationId(Long locationId) {
        return mediaRepository.findByLocationId(locationId);
    }

}
