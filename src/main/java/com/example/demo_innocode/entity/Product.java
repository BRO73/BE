package com.example.demo_innocode.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product extends AbstractEntity<Long> {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String type;

    @Column(length = 512)
    private String image;

    @Builder.Default
    @Column(nullable = false)
    private Boolean featured = false;

    @Column(name = "village")
    private String village;

    // === User liên kết ===
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
