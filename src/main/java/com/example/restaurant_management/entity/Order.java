package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "orders")
public class Order extends AbstractEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private String status = "New";

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

}