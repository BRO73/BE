package com.example.demo_innocode.service.impl;
import com.example.demo_innocode.dto.request.LocationRequestDTO;
import com.example.demo_innocode.dto.response.LocationResponseDTO;
import com.example.demo_innocode.entity.Itinerary;
import com.example.demo_innocode.entity.Location;
import com.example.demo_innocode.entity.Media;
import com.example.demo_innocode.repository.ItineraryRepository;
import com.example.demo_innocode.repository.ItineraryStopRepository;
import com.example.demo_innocode.repository.LocationRepository;
import com.example.demo_innocode.repository.MediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl {

    private final LocationRepository locationRepository;
    private final MediaRepository mediaRepository;

    public List<LocationResponseDTO> getAllLocations() {
        List<LocationResponseDTO> locationResponseDTOs = new ArrayList<>();
        List<Location> locations = locationRepository.findAll();
        for (Location location : locations) {
            List<Media> media = mediaRepository.findByLocationAndHeaderIsTrue(location)
                    .orElse(null);
            String imageUrl = "";

            if(!media.isEmpty() && media != null) {
                imageUrl = media.getFirst().getFilePath();
            }

            LocationResponseDTO locationResponseDTO = LocationResponseDTO.builder()
                    .id(location.getId())
                    .name(location.getName())
                    .type(location.getType().toString())
                    .description(location.getDescription())
                    .longitude(location.getLongitude())
                    .latitude(location.getLatitude())
                    .category(location.getType().toString())
                    .rating("5")
                    .checkIns("100")
                    .imageUrl(imageUrl)
                    .build();
            locationResponseDTOs.add(locationResponseDTO);
        }
        return locationResponseDTOs;
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public List<Media> getMediaByLocationId(Long locationId) {
        return mediaRepository.findByLocationId(locationId);
    }

    public void addLocations(List<LocationRequestDTO> locationRequestDTOs) {
        List<Location> locations = new ArrayList<>();
        for(LocationRequestDTO locationRequestDTO : locationRequestDTOs) {
            Location location = Location.builder()
                    .name(locationRequestDTO.getName())
                    .longitude(locationRequestDTO.getLongitude())
                    .latitude(locationRequestDTO.getLatitude())
                    .address(locationRequestDTO.getAddress())
                    .type(locationRequestDTO.getType())
                    .description(locationRequestDTO.getDescription())
                    .build();
            locations.add(location);
        }
        locationRepository.saveAll(locations);
    }

}
