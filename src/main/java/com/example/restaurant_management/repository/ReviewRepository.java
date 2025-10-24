package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByRatingScore(Byte ratingScore);
    List<Review> findByRatingScoreGreaterThanEqual(Byte ratingScore);
}
