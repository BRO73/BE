package com.example.demo_innocode.repository; // Thay đổi package này

import com.example.demo_innocode.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}