// Repository
package com.example.demo_innocode.repository;

import com.example.demo_innocode.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    // Tìm challenges theo location ID
    List<Challenge> findByLocationId(Long locationId);

    // Tìm challenges theo location ID với JOIN FETCH để tránh N+1 problem
    @Query("SELECT c FROM Challenge c JOIN FETCH c.location WHERE c.location.id = :locationId")
    List<Challenge> findByLocationIdWithLocation(@Param("locationId") Long locationId);

    // Tìm challenges theo location name
    @Query("SELECT c FROM Challenge c WHERE c.location.name = :locationName")
    List<Challenge> findByLocationName(@Param("locationName") String locationName);
}