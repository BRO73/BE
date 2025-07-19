package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.request.ItineraryRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ItineraryService {
    List<ItineraryResponseDTO> getItinerariesByUserId(Long userId);
    ItineraryResponseDTO createItinerary(ItineraryRequestDTO dto, Authentication authentication);
    public ItineraryResponseDTO getItineraryById(Long id);
    public void deleteItinerary(Long id);
}
