package com.example.restaurant_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "role")
public class Role extends AbstractEntity<Long> implements Serializable {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

}
