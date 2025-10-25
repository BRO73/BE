package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_session_id", nullable = false)
    private OrderSession orderSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cashier_user_id")
    private User cashierUser;

    @Column(name = "amount_paid", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @Column(name = "transaction_code")
    private String transactionCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false) // ĐỔI TỪ order_session_id
    private Bill bill;
}