package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.ReviewRequest;
import com.example.restaurant_management.dto.response.ReviewResponse;
import com.example.restaurant_management.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/5-reviews")
    public ResponseEntity<List<ReviewResponse>> getTop5Reviews() {
        return ResponseEntity.ok(reviewService.findTop5ByOrderByCreatedAtDesc());
    }
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }


    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByRating(@PathVariable Byte rating) {
        return ResponseEntity.ok(reviewService.getReviewsByRating(rating));
    }

    @GetMapping("/min-rating/{minRating}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByMinRating(@PathVariable Byte minRating) {
        return ResponseEntity.ok(reviewService.getReviewsByMinRating(minRating));
    }


}
