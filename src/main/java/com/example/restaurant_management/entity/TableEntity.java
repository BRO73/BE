package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tables")
public class TableEntity extends AbstractEntity<Long> {

    @Column(name = "table_number", nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationId", nullable = false)
    private Location location;

    @Column(nullable = false)
    private String status = "Available";

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(mappedBy = "tables")
    private List<Booking> bookings;


    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<SupportRequest> supportRequests = new ArrayList<>();
}