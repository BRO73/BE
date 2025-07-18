package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.dto.response.ActivityHistoryResponseDTO;
import com.example.demo_innocode.entity.ItineraryStop;
import com.example.demo_innocode.repository.ItineraryStopRepository;
import com.example.demo_innocode.service.ActivityHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityHistoryServiceImpl implements ActivityHistoryService {

    private final ItineraryStopRepository itineraryStopRepository;

    @Override
    public List<ActivityHistoryResponseDTO> getActivityHistoryByUserId(Long userId) {
        List<ItineraryStop> stops = itineraryStopRepository.findByItinerary_User_IdOrderByCreatedAtDesc(userId);
        return stops.stream().map(stop -> ActivityHistoryResponseDTO.builder()
                .stopId(stop.getId())
                .locationName(stop.getLocation() != null ? stop.getLocation().getName() : null) // lấy tên địa điểm
                .createdAt(stop.getCreatedAt())
                .itineraryId(stop.getItinerary().getId())
                .itineraryTitle(stop.getItinerary().getTitle())
                .build()
        ).collect(Collectors.toList());
    }
}
