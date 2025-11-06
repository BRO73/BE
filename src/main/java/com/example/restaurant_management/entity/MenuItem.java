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
@Table(name = "menu_items")
public class MenuItem extends AbstractEntity<Long> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column
    private com.example.restaurant_management.common.enums.MenuItemAvailability availability = com.example.restaurant_management.common.enums.MenuItemAvailability.AVAILABLE;

    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "menuItem", fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}