package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.PromotionRequest;
import com.example.restaurant_management.dto.request.ValidatePromotionRequest;
import com.example.restaurant_management.dto.response.PromotionResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionService {
    List<PromotionResponse> getAllPromotions();
    Optional<PromotionResponse> getPromotionById(Long id);
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    void deletePromotion(Long id);
    Optional<PromotionResponse> getPromotionByCode(String code);
    List<PromotionResponse> getPromotionsByType(String type);
    List<PromotionResponse> getActivePromotions();
    List<PromotionResponse> getPromotionsValidAt(LocalDateTime dateTime);
    PromotionResponse validateAndGetPromotion(ValidatePromotionRequest request);
}
