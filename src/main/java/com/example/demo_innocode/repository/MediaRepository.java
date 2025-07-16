package com.example.demo_innocode.repository; // Thay đổi package này

import com.example.demo_innocode.entity.Media;
import com.example.demo_innocode.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    List<Media> findByLocation(Location location);
    List<Media> findByLocationId(Long locationId);
}