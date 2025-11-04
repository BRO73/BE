package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking extends AbstractEntity<Long> {

    @ManyToMany
    @JoinTable(
            name = "booking_tables",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "table_id")
    )
    private List<TableEntity> tables;


    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;

    @Column(name = "customer_email", nullable = false, length = 100)
    private String customerEmail;

    @Column(name = "booking_time", nullable = false)
    private LocalDateTime bookingTime;

    @Column(name = "num_guests", nullable = false)
    private int numGuests;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_user_id")
    private User customerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_user_id")
    private User staffUser;
}
