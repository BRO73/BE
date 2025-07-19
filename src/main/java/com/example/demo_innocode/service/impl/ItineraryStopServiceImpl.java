package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.dto.request.ItineraryStopRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryStopResponseDTO;
import com.example.demo_innocode.entity.Itinerary;
import com.example.demo_innocode.entity.ItineraryStop;
import com.example.demo_innocode.entity.Location;
import com.example.demo_innocode.repository.ItineraryRepository;
import com.example.demo_innocode.repository.ItineraryStopRepository;
import com.example.demo_innocode.repository.LocationRepository;
import com.example.demo_innocode.service.ItineraryStopService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ItineraryStopServiceImpl implements ItineraryStopService {

    private final ItineraryRepository itineraryRepository;
    private final LocationRepository locationRepository;
    private final ItineraryStopRepository itineraryStopRepository;

    @Override
    public ItineraryStopResponseDTO addItineraryStop(ItineraryStopRequestDTO dto) {
        Itinerary itinerary = itineraryRepository.findById(dto.getItineraryId())
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
        Location location = locationRepository.findById(dto.getLocationId())
                .orElseThrow(() -> new RuntimeException("Location not found"));

        ItineraryStop stop = new ItineraryStop();
        stop.setItinerary(itinerary);
        stop.setLocation(location);
        stop.setStopOrder(dto.getStopOrder());
        stop.setPlannedAt(dto.getPlannedAt());
        stop.setEndedAt(dto.getEndedAt());
        // stop.setNote(dto.getNote()); // Nếu có trường note

        stop = itineraryStopRepository.save(stop);

        return ItineraryStopResponseDTO.builder()
                .id(stop.getId())
                .itineraryId(itinerary.getId())
                .locationId(location.getId())
                .stopOrder(stop.getStopOrder())
                .plannedAt(stop.getPlannedAt())
                .endedAt(stop.getEndedAt())
                // .note(stop.getNote()) // Nếu có trường note
                .build();
    }

}


