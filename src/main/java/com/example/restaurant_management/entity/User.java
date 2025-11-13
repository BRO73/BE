package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"})
})
public class User extends AbstractEntity<Long> implements Serializable {
    @Column(length = 50)
    private String username;

    @Column(name = "hashed_password")
    private String hashedPassword;

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

    @OneToMany(mappedBy = "staffUser", fetch = FetchType.LAZY)
    private List<SupportRequest> supportRequestsAsStaff = new ArrayList<>();

    @OneToMany(mappedBy = "customerUser", fetch = FetchType.LAZY)
    private List<SupportRequest> supportRequestsAsCustomer = new ArrayList<>();

}