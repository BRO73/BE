package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Promotion;
import com.example.restaurant_management.repository.PromotionRepository;
import com.example.restaurant_management.service.PromotionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Optional<Promotion> getPromotionById(Long id) {
        return promotionRepository.findById(id);
    }

    @Override
    public Promotion createPromotion(Promotion promotion) {
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion updatePromotion(Long id, Promotion promotion) {
        promotion.setId(id);
        return promotionRepository.save(promotion);
    }

    @Override
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public Optional<Promotion> getPromotionByCode(String code) {
        return promotionRepository.findByCode(code);
    }

    @Override
    public List<Promotion> getPromotionsByType(String type) {
        return promotionRepository.findByPromotionType(type);
    }

    @Override
    public List<Promotion> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByEndDateAfter(now);
    }

    @Override
    public List<Promotion> getPromotionsValidAt(LocalDateTime dateTime) {
        return promotionRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(dateTime, dateTime);
    }
}
