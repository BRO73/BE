package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.ReviewRequest;
import com.example.restaurant_management.dto.response.ReviewResponse;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.Review;
import com.example.restaurant_management.entity.User;
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
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private ReviewResponse mapToResponse(Review review) {
        var user = review.getCustomerUser();

        String customerName = "Unknown";
        String customerEmail = "N/A";

        if (user != null) {
            // Nếu user có liên kết với customer (quan hệ 1-1 trong bảng customers)
            if (user.getCustomer() != null) {
                Customer customer = user.getCustomer();
                if (customer.getFullName() != null)
                    customerName = customer.getFullName();
                if (customer.getEmail() != null)
                    customerEmail = customer.getEmail();
            } else {
                // fallback — chỉ có username nếu là staff/admin
                if (user.getUsername() != null)
                    customerName = user.getUsername();
            }
        }

        return ReviewResponse.builder()
                .id(review.getId())
                .orderId(review.getOrder().getId())
                .ratingScore((byte) review.getRatingScore())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .deleted(review.isDeleted())
                .activated(review.isActivated())
                .customerName(customerName)
                .customerEmail(customerEmail)
                .build();
    }


    @Override
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReviewResponse> getReviewById(Long id) {
        return reviewRepository.findById(id).map(this::mapToResponse);
    }

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        User customer = order.getCustomerUser();

        Review review = Review.builder()
                .order(order)
                .customerUser(customer)
                .ratingScore(request.getRatingScore())
                .comment(request.getComment())
                .build();

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse updateReview(Long id, ReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        review.setRatingScore(request.getRatingScore());
        review.setComment(request.getComment());

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    @Override
    public Optional<ReviewResponse> getReviewByOrder(Long orderId) {
        return reviewRepository.findByOrderId(orderId).map(this::mapToResponse);
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
}
