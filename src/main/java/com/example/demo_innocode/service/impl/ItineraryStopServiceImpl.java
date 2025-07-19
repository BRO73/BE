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

import java.util.List;

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
        stop.setStartedAt(dto.getStartedAt());
        stop.setEndedAt(dto.getEndedAt());

        ItineraryStop savedStop = itineraryStopRepository.save(stop);
        return toDto(savedStop);
    }

    @Override
    public ItineraryStopResponseDTO updateItineraryStop(Long id, ItineraryStopRequestDTO dto) {
        ItineraryStop stop = itineraryStopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ItineraryStop not found"));

        stop.setStopOrder(dto.getStopOrder());
        stop.setStartedAt(dto.getStartedAt());
        stop.setEndedAt(dto.getEndedAt());

        ItineraryStop updatedStop = itineraryStopRepository.save(stop);
        return toDto(updatedStop);
    }

    @Override
    public void deleteItineraryStop(Long id) {
        if (!itineraryStopRepository.existsById(id)) {
            throw new RuntimeException("ItineraryStop not found");
        }
        itineraryStopRepository.deleteById(id);
    }

    @Override
    public List<ItineraryStopResponseDTO> getAllItineraryStops() {
        return itineraryStopRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // ✅ Reusable private mapper
    private ItineraryStopResponseDTO toDto(ItineraryStop stop) {
        return ItineraryStopResponseDTO.builder()
                .id(stop.getId())
                .itineraryId(stop.getItinerary().getId())
                .locationId(stop.getLocation().getId())
                .stopOrder(stop.getStopOrder())
                .startedAt(stop.getStartedAt())
                .endedAt(stop.getEndedAt())
                .build();
    }

    @Override
    public List<ItineraryStopResponseDTO> getStopsByItineraryId(Long itineraryId) {
        Itinerary itinerary = itineraryRepository.findById(itineraryId)
                .orElseThrow(() -> new RuntimeException("Itinerary not found"));
        return itineraryStopRepository.findByItinerary(itinerary)
                .stream()
                .map(this::toDto)
                .toList();
    }

}
