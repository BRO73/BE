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
@Table(name = "stores")
public class Store extends AbstractEntity<Long> implements Serializable {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column
    private String description;

}
