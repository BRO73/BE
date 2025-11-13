package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.ReviewRequest;
import com.example.restaurant_management.dto.response.ReviewResponse;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<ReviewResponse> getAllReviews();
    ReviewResponse createReview(ReviewRequest request);
    List<ReviewResponse> getReviewsByRating(Byte rating);
    List<ReviewResponse> getReviewsByMinRating(Byte minRating);
    List<ReviewResponse> findTop5ByOrderByCreatedAtDesc();
}
