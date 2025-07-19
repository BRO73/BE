package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.request.ItineraryStopRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryStopResponseDTO;
import com.example.demo_innocode.entity.Itinerary;
import com.example.demo_innocode.entity.ItineraryStop;

import java.util.List;

import java.util.List;

public interface ItineraryStopService {
    ItineraryStopResponseDTO addItineraryStop(ItineraryStopRequestDTO dto);

    public ItineraryStopResponseDTO updateItineraryStop(Long id, ItineraryStopRequestDTO dto);
    public void deleteItineraryStop(Long id);
    List<ItineraryStopResponseDTO> getAllItineraryStops();
    List<ItineraryStopResponseDTO> getStopsByItineraryId(Long itineraryId);

}
