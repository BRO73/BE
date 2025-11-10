package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.ReviewRequest;
import com.example.restaurant_management.dto.response.ReviewResponse;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewResponse> getAllReviews();
    Optional<ReviewResponse> getReviewById(Long id);
    ReviewResponse createReview(ReviewRequest request);
    ReviewResponse updateReview(Long id, ReviewRequest request);
    void deleteReview(Long id);
    Optional<ReviewResponse> getReviewByOrder(Long orderId);
    List<ReviewResponse> getReviewsByRating(Byte rating);
    List<ReviewResponse> getReviewsByMinRating(Byte minRating);
}
