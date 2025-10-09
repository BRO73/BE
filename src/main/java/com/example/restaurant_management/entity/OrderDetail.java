package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "order_details")
public class OrderDetail extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "price_at_order", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAtOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private com.example.restaurant_management.common.enums.OrderItemStatus status = com.example.restaurant_management.common.enums.OrderItemStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;


}