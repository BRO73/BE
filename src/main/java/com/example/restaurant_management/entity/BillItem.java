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
@Table(name = "bill_items")
public class BillItem extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bill_id", nullable = false)
    private Bill bill; // Thuộc hóa đơn nào

    // ----- LIÊN KẾT VÀNG -----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", nullable = false)
    private OrderDetail originalOrderDetail; // Trỏ về món ăn GỐC

    // Số tiền của món gốc được trả trong bill NÀY
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
}