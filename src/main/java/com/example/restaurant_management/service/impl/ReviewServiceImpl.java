package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.ReviewRequest;
import com.example.restaurant_management.dto.response.ReviewResponse;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.Review;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.repository.OrderRepository;
import com.example.restaurant_management.repository.ReviewRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.ReviewService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    public ReviewServiceImpl(ReviewRepository reviewRepository, CustomerRepository customerRepository) {
        this.reviewRepository = reviewRepository;
        this.customerRepository = customerRepository;
    }

    private ReviewResponse mapToResponse(Review review) {
        Customer customer = review.getCustomer();

        String customerName = "Unknown";
        String customerEmail = "N/A";
        String customerPhone = "N/A";

        if (customer != null) {
            if (customer.getFullName() != null && !customer.getFullName().isBlank()) {
                customerName = customer.getFullName();
            }
            if (customer.getEmail() != null && !customer.getEmail().isBlank()) {
                customerEmail = customer.getEmail();
            }
            if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().isBlank()) {
                customerPhone = customer.getPhoneNumber();
            }
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .ratingScore((byte) review.getRatingScore())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .deleted(review.isDeleted())
                .activated(review.isActivated())
                .customerName(customerName)
                .customerEmail(customerEmail)
                .customerPhone(customerPhone)
                .build();
    }



    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Review review = Review.builder()
                .customer(customer)
                .ratingScore(request.getRatingScore() != null ? request.getRatingScore() : 0) // fallback 0 nếu null
                .comment(request.getComment())
                .build();

        return mapToResponse(reviewRepository.save(review));
    }



    @Override
    public List<ReviewResponse> getReviewsByRating(Byte rating) {
        return reviewRepository.findByRatingScore(rating)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByMinRating(Byte minRating) {
        return reviewRepository.findByRatingScoreGreaterThanEqual(minRating)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> findTop5ByOrderByCreatedAtDesc() {
        return reviewRepository.findAll().stream()
                .limit(5)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

}
