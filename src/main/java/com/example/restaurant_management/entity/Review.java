package com.example.restaurant_management.entity;

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
    @JoinColumn(name = "order_session_id", nullable = false, unique = true) // 'name' là tên cột FK trong DB
    private OrderSession orderSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_user_id", nullable = false)
    private User customerUser;

    @Column(name = "rating_score", nullable = false)
    private int ratingScore;

    @Column(columnDefinition = "TEXT")
    private String comment;
}