package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username", "store_id"})
})
public class User extends AbstractEntity<Long> implements Serializable {

    public enum UserType {
        STAFF,
        CUSTOMER
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false, length = 20)
    private UserType userType;

    @Column(length = 50)
    private String username;

    @Column(name = "hashed_password")
    private String hashedPassword;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber;

    @Column(name = "otp_code", length = 10)
    private String otpCode;

    @Column(name = "otp_expiry_time")
    private LocalDateTime otpExpiryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Staff staff;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Customer customer;

    @OneToMany(mappedBy = "staffUser", fetch = FetchType.LAZY)
    private List<Order> ordersAsStaff = new ArrayList<>();

    @OneToMany(mappedBy = "customerUser", fetch = FetchType.LAZY)
    private List<Order> ordersAsCustomer = new ArrayList<>();

    @OneToMany(mappedBy = "cashierUser", fetch = FetchType.LAZY)
    private List<Transaction> transactionsAsCashier = new ArrayList<>();

    @OneToMany(mappedBy = "staffUser", fetch = FetchType.LAZY)
    private List<Booking> bookingsAsStaff = new ArrayList<>();

    @OneToMany(mappedBy = "customerUser", fetch = FetchType.LAZY)
    private List<Booking> bookingsAsCustomer = new ArrayList<>();

    @OneToMany(mappedBy = "customerUser", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "staffUser", fetch = FetchType.LAZY)
    private List<SupportRequest> supportRequestsAsStaff = new ArrayList<>();

    @OneToMany(mappedBy = "customerUser", fetch = FetchType.LAZY)
    private List<SupportRequest> supportRequestsAsCustomer = new ArrayList<>();

}