package com.example.demo_innocode.repository;

import com.example.demo_innocode.entity.Media;
import com.example.demo_innocode.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByLocation(Location location);
    List<Media> findByLocationId(Long locationId);
    Optional<List<Media>> findByLocationAndHeaderIsTrue(Location location);
}