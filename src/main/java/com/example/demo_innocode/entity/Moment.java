package com.example.demo_innocode.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "moments")
public class Moment extends AbstractEntity<Long> {

    @Column(name = "media_url", nullable = false)
    private String mediaUrl;

    @Column(name = "media_type", nullable = false, length = 50)
    private String mediaType; // "IMAGE", "VIDEO", "AUDIO"

    @Column(columnDefinition = "TEXT")
    private String caption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "itinerary_stop_id", nullable = false)
    private ItineraryStop itineraryStop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}