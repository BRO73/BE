package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.PromotionRequest;
import com.example.restaurant_management.dto.response.PromotionResponse;
import com.example.restaurant_management.entity.Promotion;
import com.example.restaurant_management.repository.PromotionRepository;
import com.example.restaurant_management.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    // ✅ Helper method để chuyển entity -> response
    private PromotionResponse fromEntity(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .code(promotion.getCode())
                .description(promotion.getDescription())
                .promotionType(promotion.getPromotionType())
                .value(promotion.getValue())
                .minSpend(promotion.getMinSpend())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .usageLimit(promotion.getUsageLimit())
                .createdAt(promotion.getCreatedAt())
                .updatedAt(promotion.getUpdatedAt())
                .deleted(promotion.isDeleted())
                .activated(promotion.isActivated())
                .build();
    }

    // ✅ Helper method để chuyển request -> entity
    private Promotion toEntity(PromotionRequest request) {
        return Promotion.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .promotionType(request.getPromotionType())
                .value(request.getValue())
                .minSpend(request.getMinSpend())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .usageLimit(request.getUsageLimit())
                .build();
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::fromEntity)
                .toList();
    }

    @Override
    public Optional<PromotionResponse> getPromotionById(Long id) {
        return promotionRepository.findById(id).map(this::fromEntity);
    }

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        Promotion promotion = toEntity(request);
        return fromEntity(promotionRepository.save(promotion));
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));

        promotion.setName(request.getName());
        promotion.setCode(request.getCode());
        promotion.setDescription(request.getDescription());
        promotion.setPromotionType(request.getPromotionType());
        promotion.setValue(request.getValue());
        promotion.setMinSpend(request.getMinSpend());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        promotion.setUsageLimit(request.getUsageLimit());

        return fromEntity(promotionRepository.save(promotion));
    }

    @Override
    public void deletePromotion(Long id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public Optional<PromotionResponse> getPromotionByCode(String code) {
        return promotionRepository.findByCode(code).map(this::fromEntity);
    }

    @Override
    public List<PromotionResponse> getPromotionsByType(String type) {
        return promotionRepository.findByPromotionType(type).stream()
                .map(this::fromEntity)
                .toList();
    }

    @Override
    public List<PromotionResponse> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();
        return promotionRepository.findByEndDateAfter(now).stream()
                .map(this::fromEntity)
                .toList();
    }

    @Override
    public List<PromotionResponse> getPromotionsValidAt(LocalDateTime dateTime) {
        return promotionRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(dateTime, dateTime).stream()
                .map(this::fromEntity)
                .toList();
    }
}
