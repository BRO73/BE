package com.example.restaurant_management.entity;

import com.example.restaurant_management.common.enums.StaffRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_account_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_user_account_staff", columnNames = "staff_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Username đăng nhập (duy nhất) */
    @Column(nullable = false, length = 120)
    private String username;

    /** MẬT KHẨU ĐÃ HASH (BCrypt) */
    @Column(nullable = false, length = 120)
    private String password;

    /** Quyền theo role của nhân viên */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private StaffRole role;

    /** Liên kết 1-1 với Staff (mỗi staff 1 account) */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_account_staff"))
    private Staff staff;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
