package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@jakarta.persistence.Table(name = "tables")
public class Table extends AbstractEntity<Long> {

    @Column(name = "table_number", nullable = false, unique = true)
    private String tableNumber;

    @Column(nullable = false)
    private Integer capacity;

    private String location;

    @Column(nullable = false)
    private String status = "Available";
}