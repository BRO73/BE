package com.example.demo_innocode.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "badges")
public class Badge extends AbstractEntity<Long> {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon_url")
    private String iconUrl;

    @OneToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;
}