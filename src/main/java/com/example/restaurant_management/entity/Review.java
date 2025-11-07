package com.example.restaurant_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "reviews")
public class Review extends AbstractEntity<Long> {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"reviews", "table", "staffUser", "customerUser"})
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_user_id", nullable = false)
    private User customerUser;

    @Column(name = "rating_score", nullable = false)
    private int ratingScore;

    @Column(columnDefinition = "TEXT")
    private String comment;
}