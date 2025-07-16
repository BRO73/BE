package com.example.demo_innocode.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "virtual_tours")
public class VirtualTour extends AbstractEntity<Long> {

    @Column(nullable = false)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String description;
}
