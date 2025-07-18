package com.example.demo_innocode.service.impl;

import com.example.demo_innocode.dto.request.MediaRequestDTO;
import com.example.demo_innocode.dto.response.MediaResponseDTO;
import com.example.demo_innocode.entity.Location;
import com.example.demo_innocode.entity.Media;
import com.example.demo_innocode.repository.LocationRepository;
import com.example.demo_innocode.repository.MediaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MediaServiceImpl {

    private final MediaRepository mediaRepository;
    private final LocationRepository locationRepository;

    public List<MediaResponseDTO> createMedia(List<MediaRequestDTO> requestList) {
        List<MediaResponseDTO> responseList = new ArrayList<>();

        for (MediaRequestDTO request : requestList) {
            Location location = locationRepository.findById(request.getLocationId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy địa điểm với ID: " + request.getLocationId()));

            Media media = new Media();
            media.setFilePath(request.getFilePath());
            media.setHeader(request.isHeader());
            media.setFileType(request.getFileType());
            media.setDescription(request.getDescription());
            media.setLocation(location);

            media = mediaRepository.save(media);

            MediaResponseDTO response = new MediaResponseDTO();
            response.setId(media.getId());
            response.setFilePath(media.getFilePath());
            response.setHeader(media.isHeader());
            response.setFileType(media.getFileType());
            response.setDescription(media.getDescription());
            response.setLocationId(location.getId());
            response.setLocationName(location.getName());

            responseList.add(response);
        }

        return responseList;
    }
}