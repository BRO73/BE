package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_sessions")
public class OrderSession extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private TableEntity table;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_user_id")
    private User customerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_user_id")
    private User staffUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "orderSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "orderSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
    
    @OneToOne(mappedBy = "orderSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Review review;

    @OneToMany(mappedBy = "originatingSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bill> generatedBills = new ArrayList<>();
}