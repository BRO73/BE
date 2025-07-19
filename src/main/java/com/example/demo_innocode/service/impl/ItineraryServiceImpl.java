package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.common.constant.ErrorEnum;
import com.example.demo_innocode.common.exception.InnoException;
import com.example.demo_innocode.dto.request.ItineraryRequestDTO;
import com.example.demo_innocode.dto.response.ItineraryResponseDTO;
import com.example.demo_innocode.entity.Itinerary;
import com.example.demo_innocode.entity.User;
import com.example.demo_innocode.model.CredentialPayload;
import com.example.demo_innocode.repository.ItineraryRepository;
import com.example.demo_innocode.repository.ItineraryStopRepository;
import com.example.demo_innocode.repository.UserRepository;
import com.example.demo_innocode.service.ItineraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItineraryServiceImpl implements ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final UserRepository userRepository;
    private final ItineraryStopRepository itineraryStopRepository;

    @Override
    public List<ItineraryResponseDTO> getItinerariesByUserId(Long userId) {
        return itineraryRepository.findByUserIdOrderByStartDateAsc(userId)
                .stream()
                .map(itinerary -> toDto(itinerary, true))
                .collect(Collectors.toList());
    }

    @Override
    public ItineraryResponseDTO createItinerary(ItineraryRequestDTO dto, Authentication authentication) {
        if (authentication == null) {
            throw new InnoException(ErrorEnum.ACCESS_DENIED);
        }

        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        Long id = credentialPayload.getUserId();

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Itinerary itinerary = new Itinerary();
        itinerary.setTitle(dto.getTitle());
        itinerary.setDescription(dto.getDescription());
        itinerary.setStartDate(dto.getStartDate());
        itinerary.setEndDate(dto.getEndDate());
        itinerary.setStatus(dto.getStatus());
        itinerary.setUser(user);
        itinerary = itineraryRepository.save(itinerary);
        return toDto(itinerary, true);
    }

    private ItineraryResponseDTO toDto(Itinerary itinerary, boolean b) {
        return ItineraryResponseDTO.builder()
                .id(itinerary.getId())
                .title(itinerary.getTitle())
                .description(itinerary.getDescription())
                .startDate(itinerary.getStartDate())
                .endDate(itinerary.getEndDate())
                .status(itinerary.getStatus())
                .build();
    }

    @Override
    public ItineraryResponseDTO getItineraryById(Long id) {
        Itinerary itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found with id: " + id));
        return toDto(itinerary, true);
    }

    @Override
    public void deleteItinerary(Long id) {
        Itinerary itinerary = itineraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Itinerary not found with id: " + id));
        if (!itineraryRepository.existsById(id)) {
            throw new RuntimeException("Itinerary not found with id: " + id);
        }

        itineraryStopRepository.deleteByItinerary(itinerary);
        itineraryRepository.deleteById(id);
    }

}
