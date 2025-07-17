package com.example.demo_innocode.controller;

import com.example.demo_innocode.dto.request.MediaRequestDTO;
import com.example.demo_innocode.dto.response.MediaResponseDTO;
import com.example.demo_innocode.service.impl.MediaServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MediaController {

    private final MediaServiceImpl mediaService;
    @PostMapping("/media/batch")
    public ResponseEntity<List<MediaResponseDTO>> createMediaBatch(@RequestBody List<MediaRequestDTO> requestList) {
        List<MediaResponseDTO> responses = mediaService.createMedia(requestList);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

}
