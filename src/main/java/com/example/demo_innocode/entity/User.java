package com.example.demo_innocode.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Getter
@Setter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractEntity<Long> {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String fullName;

    private String phone;

    @Column(nullable = false)
    private String role; // e.g., "user" or "admin" or "owner"

    @OneToMany(mappedBy = "user")
    private List<Itinerary> itineraries;

    private String avatar; // đường dẫn ảnh đại diện
    private String location; // địa chỉ/living
    private String bio;
}
