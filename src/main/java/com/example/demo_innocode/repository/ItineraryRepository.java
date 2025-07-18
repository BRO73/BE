package com.example.demo_innocode.repository;

import com.example.demo_innocode.entity.Itinerary;
import com.example.demo_innocode.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
    List<Itinerary> findByUserIdOrderByStartDateAsc(Long userId);
    List<Itinerary> findByUserOrderByStartDateAsc(User user); // optional nếu cần
}
