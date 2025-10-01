package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tables")
public class TableEntity extends AbstractEntity<Long> {
    @Column(name = "table_number", nullable = false, unique = true, length = 10)
    private String tableNumber;

    @Column(nullable = false)
    private int capacity;

    private String location;

    @Column(nullable = false, length = 20)
    private String status;

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<SupportRequest> supportRequests = new ArrayList<>();
}