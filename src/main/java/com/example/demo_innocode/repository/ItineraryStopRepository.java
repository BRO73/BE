package com.example.demo_innocode.repository;

import com.example.demo_innocode.entity.ItineraryStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryStopRepository extends JpaRepository<ItineraryStop, Long> {
    // Lấy tất cả stops theo userId qua liên kết Itinerary -> User
    List<ItineraryStop> findByItinerary_User_IdOrderByCreatedAtDesc(Long userId);
}
