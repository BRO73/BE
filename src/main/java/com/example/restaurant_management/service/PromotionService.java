package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Promotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromotionService {
    List<Promotion> getAllPromotions();
    Optional<Promotion> getPromotionById(Long id);
    Promotion createPromotion(Promotion promotion);
    Promotion updatePromotion(Long id, Promotion promotion);
    void deletePromotion(Long id);
    Optional<Promotion> getPromotionByCode(String code);
    List<Promotion> getPromotionsByType(String type);
    List<Promotion> getActivePromotions();
    List<Promotion> getPromotionsValidAt(LocalDateTime dateTime);
}
