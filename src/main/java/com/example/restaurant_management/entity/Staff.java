package com.example.restaurant_management.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "staff")
public class Staff extends AbstractEntity<Long> {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", unique = true, length = 15)
    private String phoneNumber;

    @Column(name = "role")
    private String role;

    // Staff.java
    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "password_text", length = 120)
    private String passwordText;

    @OneToOne(mappedBy = "staff", fetch = FetchType.LAZY)
    private UserAccount userAccount;

}