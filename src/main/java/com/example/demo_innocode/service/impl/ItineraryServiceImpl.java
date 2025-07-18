package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.dto.request.ItineraryRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryResponseDTO;
import com.example.demo_innocode.entity.Itinerary;
import com.example.demo_innocode.entity.User;
import com.example.demo_innocode.repository.ItineraryRepository;
import com.example.demo_innocode.repository.UserRepository;
import com.example.demo_innocode.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItineraryResponseDTO> getItinerariesByUserId(Long userId) {
        return itineraryRepository.findByUserIdOrderByStartDateAsc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItineraryResponseDTO createItinerary(ItineraryRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Itinerary itinerary = new Itinerary();
        itinerary.setTitle(dto.getTitle());
        itinerary.setDescription(dto.getDescription());
        itinerary.setStartDate(dto.getStartDate());
        itinerary.setEndDate(dto.getEndDate());
        itinerary.setStatus(dto.getStatus());
        itinerary.setUser(user);
        itinerary = itineraryRepository.save(itinerary);
        return toDto(itinerary);
    }

    private ItineraryResponseDTO toDto(Itinerary itinerary) {
        return ItineraryResponseDTO.builder()
                .id(itinerary.getId())
                .title(itinerary.getTitle())
                .description(itinerary.getDescription())
                .startDate(itinerary.getStartDate())
                .endDate(itinerary.getEndDate())
                .status(itinerary.getStatus())
                .build();
    }
}
