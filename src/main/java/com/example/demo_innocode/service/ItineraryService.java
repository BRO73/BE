package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.request.ItineraryRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryResponseDTO;

import java.util.List;

public interface ItineraryService {
    List<ItineraryResponseDTO> getItinerariesByUserId(Long userId);
    ItineraryResponseDTO createItinerary(ItineraryRequestDTO dto);
}
