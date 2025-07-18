package com.example.demo_innocode.service;

import com.example.demo_innocode.dto.response.ActivityHistoryResponseDTO;

import java.util.List;

public interface ActivityHistoryService {
    List<ActivityHistoryResponseDTO> getActivityHistoryByUserId(Long userId);
}
