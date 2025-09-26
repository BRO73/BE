package com.example.restaurant_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "permission")
public class Permission extends AbstractEntity<Long> implements Serializable {

    @Column(name = "name", unique = true, length = 50)
    private String name;

    @Column(name = "description")
    private String description;

}
