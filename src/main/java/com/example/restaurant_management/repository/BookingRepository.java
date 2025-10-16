package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Ví dụ: tìm tất cả booking theo trạng thái
    List<Booking> findByStatus(String status);

    // Ví dụ: tìm booking theo khoảng thời gian
    List<Booking> findByBookingTimeBetween(LocalDateTime start, LocalDateTime end);

    // Ví dụ: tìm booking theo số điện thoại khách hàng
    List<Booking> findByCustomerPhone(String customerPhone);

    @Query("SELECT b FROM Booking b WHERE b.customerUser.username = :username AND b.status = 'Confirmed'")
    Optional<Booking> findConfirmedBooking(@Param("username") String username);

    @Query("SELECT b FROM Booking b WHERE b.customerUser.username = :username AND b.table.id = :tableId AND b.status = 'Confirmed'")
    Optional<Booking> findConfirmedBookingByTable(@Param("username") String username, @Param("tableId") Long tableId);
}
