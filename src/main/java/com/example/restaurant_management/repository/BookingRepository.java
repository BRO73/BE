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

    // ✅ Lấy danh sách booking có chứa bàn cụ thể trong cùng ngày
    @Query("""
        SELECT DISTINCT b FROM Booking b
        JOIN b.tables t
        WHERE t.id = :tableId
        AND FUNCTION('DATE', b.bookingTime) = FUNCTION('DATE', :bookingTime)
        """)
    List<Booking> findByTableAndDay(
            @Param("tableId") Long tableId,
            @Param("bookingTime") LocalDateTime bookingTime
    );

    // ✅ Kiểm tra xem bàn đã được đặt vào ngày đó chưa
    @Query("""
        SELECT COUNT(b) > 0 
        FROM Booking b
        JOIN b.tables t
        WHERE t.id = :tableId
        AND FUNCTION('DATE', b.bookingTime) = FUNCTION('DATE', :bookingTime)
        """)
    boolean existsBookingForTableOnDate(
            @Param("tableId") Long tableId,
            @Param("bookingTime") LocalDateTime bookingTime
    );

    // ✅ Tìm booking theo user khách hàng
    List<Booking> findByCustomerUserId(Long customerUserId);

    // ❌ Không thể dùng findByTableId vì Booking không có field table
    // ✅ Thay thế bằng custom query join với tables
    @Query("""
        SELECT DISTINCT b FROM Booking b
        JOIN b.tables t
        WHERE t.id = :tableId
        """)
    List<Booking> findByTableId(@Param("tableId") Long tableId);

    // ✅ Tìm theo trạng thái
    List<Booking> findByStatus(String status);

    // ✅ Tìm booking theo khoảng thời gian
    @Query("""
        SELECT b FROM Booking b
        WHERE b.bookingTime BETWEEN :start AND :end
        """)
    List<Booking> findBookingsBetween(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
