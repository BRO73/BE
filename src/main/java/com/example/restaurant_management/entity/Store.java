package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "stores")
public class Store extends AbstractEntity<Long> {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String address;

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<User> staffUsers = new ArrayList<>();

    @OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
    private List<Staff> staffs = new ArrayList<>();
}