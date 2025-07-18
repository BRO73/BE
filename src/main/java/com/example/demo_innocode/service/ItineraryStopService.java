package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.request.ItineraryStopRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryStopResponseDTO;

public interface ItineraryStopService {
    ItineraryStopResponseDTO addItineraryStop(ItineraryStopRequestDTO dto);
}
