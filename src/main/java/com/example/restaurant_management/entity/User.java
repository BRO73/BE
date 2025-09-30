package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User extends AbstractEntity<Long> implements Serializable {

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "hashed_password", nullable = false)
    private String hashedPassword;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 15) // ✅ bỏ unique nếu không cần
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id") // ✅ thêm nullable = false nếu muốn bắt buộc
    private Store store;
}
