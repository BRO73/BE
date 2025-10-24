package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tables")
public class TableEntity extends AbstractEntity<Long> {

    @Column(name = "table_number", nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private String status = "Available";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_order_session_id", unique = true)
    private OrderSession currentOrderSession;

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "table", fetch = FetchType.LAZY)
    private List<SupportRequest> supportRequests = new ArrayList<>();
}