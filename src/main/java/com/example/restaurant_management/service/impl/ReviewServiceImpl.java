package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Review;
import com.example.restaurant_management.repository.ReviewRepository;
import com.example.restaurant_management.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Override
    public Optional<Review> getReviewById(Long id) {
        return reviewRepository.findById(id);
    }

    @Override
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    @Override
    public Review updateReview(Long id, Review review) {
        review.setId(id);
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public List<Review> getReviewsByRating(Byte rating) {
        return reviewRepository.findByRatingScore(rating);
    }

    @Override
    public List<Review> getReviewsByMinRating(Byte minRating) {
        return reviewRepository.findByRatingScoreGreaterThanEqual(minRating);
    }
}
