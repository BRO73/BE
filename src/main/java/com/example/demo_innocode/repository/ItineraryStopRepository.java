package com.example.demo_innocode.repository;

import com.example.demo_innocode.entity.Itinerary;
import com.example.demo_innocode.entity.ItineraryStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryStopRepository extends JpaRepository<ItineraryStop, Long> {
    List<ItineraryStop> findByItinerary_User_IdOrderByCreatedAtDesc(Long userId);
    void deleteByItinerary(Itinerary itinerary);
    List<ItineraryStop> findByItinerary(Itinerary itinerary);
}
