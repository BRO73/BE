package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString
@Table(name = "floor_elements")
public class FloorElement extends AbstractEntity<String> {

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private double x;

    @Column(nullable = false)
    private double y;

    @Column(nullable = false)
    private double width;

    @Column(nullable = false)
    private double height;

    @Column(nullable = false)
    private double rotation;

    @Column(length = 20)
    private String color;

    @Column(length = 100)
    private String label;

    // ====== NEW FIELDS ======

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = true)
    private TableEntity table;

}
