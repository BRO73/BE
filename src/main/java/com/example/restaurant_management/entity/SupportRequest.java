package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "support_requests")
public class SupportRequest extends AbstractEntity<Long> {

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    @Column(nullable = false)
    private String status = "Pending";

    @Column(columnDefinition = "TEXT")
    private String details;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
}