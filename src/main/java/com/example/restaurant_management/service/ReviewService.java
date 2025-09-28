package com.example.restaurant_management.service;

import com.example.restaurant_management.entity.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Review> getAllReviews();
    Optional<Review> getReviewById(Long id);
    Review createReview(Review review);
    Review updateReview(Long id, Review review);
    void deleteReview(Long id);
    Optional<Review> getReviewByOrder(Long orderId);
    List<Review> getReviewsByRating(Byte rating);
    List<Review> getReviewsByMinRating(Byte minRating);
}
