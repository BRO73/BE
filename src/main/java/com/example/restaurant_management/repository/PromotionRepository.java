package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);
    List<Promotion> findByPromotionType(String promotionType);
    List<Promotion> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDateTime startDate, LocalDateTime endDate);
    List<Promotion> findByEndDateAfter(LocalDateTime currentDate);
}
