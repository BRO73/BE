package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bills")
public class Bill extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_session_id", nullable = false)
    private OrderSession originatingSession; // Bill này của bàn nào

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount; // Tổng tiền của cái bill này

    @Column(nullable = false, length = 20)
    private String status; // PENDING, PAID, CANCELLED

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BillItem> billItems = new ArrayList<>();

    // Transaction giờ liên kết với Bill
    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transaction> transactions = new ArrayList<>();
}