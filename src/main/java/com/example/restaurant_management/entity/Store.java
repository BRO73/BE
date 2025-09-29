package com.example.restaurant_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "stores")
public class Store extends AbstractEntity<Long> implements Serializable {

    @Column(name = "store_name", nullable = false, unique = true, length = 100)
    private String storeName;
}
