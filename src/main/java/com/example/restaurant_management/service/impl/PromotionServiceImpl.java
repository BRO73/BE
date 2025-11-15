package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.exception.promotion.PromotionValidationException;
import com.example.restaurant_management.common.exception.promotion.*;
import com.example.restaurant_management.dto.request.PromotionRequest;
import com.example.restaurant_management.dto.request.ValidatePromotionRequest;
import com.example.restaurant_management.dto.response.PromotionResponse;
import com.example.restaurant_management.entity.Promotion;
import com.example.restaurant_management.repository.PromotionRepository;
import com.example.restaurant_management.repository.TransactionRepository;
import com.example.restaurant_management.service.PromotionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final TransactionRepository transactionRepository;
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

    @Override
    @Transactional()
    public PromotionResponse validateAndGetPromotion(ValidatePromotionRequest request) {
        String code = request.getCode();
        BigDecimal totalAmount = request.getTotalAmount();
        Long userId = request.getUserId(); // Lấy userId từ request
        String paidStatus = "PAID"; // Định nghĩa trạng thái đã thanh toán

        if (userId == null) {
            // Nếu không có userId (khách vãng lai), không cho dùng mã
            throw new PromotionValidationException("Bạn cần cung cấp thông tin khách hàng để dùng mã.");
        }

        // 1. Tìm promotion (Giữ nguyên)
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(PromotionNotFoundException::new);

        // 2. Kiểm tra activated (Giữ nguyên)
        if (!promotion.isActivated()) {
            throw new PromotionNotActiveException();
        }

        // 3. Kiểm tra ngày hiệu lực (Giữ nguyên)
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(promotion.getStartDate())) {
            throw new PromotionValidationException("Mã giảm giá chưa có hiệu lực");
        }
        if (now.isAfter(promotion.getEndDate())) {
            throw new PromotionExpiredException();
        }

        boolean alreadyUsed = transactionRepository.existsByPromotionIdAndOrderCustomerUserIdAndPaymentStatus(
                promotion.getId(),
                userId,
                paidStatus
        );
        if (alreadyUsed) {
            throw new PromotionValidationException("Bạn đã sử dụng mã giảm giá này rồi.");
        }
        // 6. Kiểm tra chi tiêu tối thiểu (Giữ nguyên)
        if (totalAmount.compareTo(promotion.getMinSpend()) < 0) {
            throw new PromotionMinSpendException(
                    String.format("Đơn hàng tối thiểu %sđ để áp dụng mã giảm giá",
                            promotion.getMinSpend().toBigInteger())
            );
        }

        return fromEntity(promotion); // Giả sử fromEntity là hàm map của bạn
    }
}
