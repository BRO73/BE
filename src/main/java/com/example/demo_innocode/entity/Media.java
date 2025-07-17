package com.example.demo_innocode.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "media")
public class Media extends AbstractEntity<Long> {

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "header")
    private boolean header;

    @Column(name = "file_type", nullable = false, length = 10)
    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
}
