package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        SELECT b FROM Booking b 
        WHERE b.table.id = :tableId 
        AND FUNCTION('DATE', b.bookingTime) = FUNCTION('DATE', :bookingTime)
        """)
    List<Booking> findByTableAndDay(
            @Param("tableId") Long tableId,
            @Param("bookingTime") LocalDateTime bookingTime
    );

    @Query("""
        SELECT COUNT(b) > 0 
        FROM Booking b 
        WHERE b.table.id = :tableId 
        AND FUNCTION('DATE', b.bookingTime) = FUNCTION('DATE', :bookingTime)
        """)
    boolean existsBookingForTableOnDate(
            @Param("tableId") Long tableId,
            @Param("bookingTime") LocalDateTime bookingTime
    );

    List<Booking> findByCustomerUserId(Long customerUserId);

    List<Booking> findByTableId(Long tableId);

    List<Booking> findByStatus(String status);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.bookingTime BETWEEN :start AND :end
        """)
    List<Booking> findBookingsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}