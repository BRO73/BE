package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "reviews")
public class Review extends AbstractEntity<Long> {

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(name = "rating_score", nullable = false)
    private Byte ratingScore;

    @Column(columnDefinition = "TEXT")
    private String comment;
}