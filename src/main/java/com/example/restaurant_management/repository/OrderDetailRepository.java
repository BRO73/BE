package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);
    List<OrderDetail> findByMenuItemId(Long menuItemId);
    List<OrderDetail> findByStatus(String status);

    // Lấy TẤT CẢ order details của 1 session
    List<OrderDetail> findByOrderOrderSessionId(Long sessionId);

    // Lấy các order details CHƯA TRẢ HẾT của 1 món, theo FIFO
    @Query("SELECT od FROM OrderDetail od " +
            "WHERE od.order.orderSession.id = :sessionId " +
            "AND od.menuItem.id = :menuItemId " +
            "AND (od.priceAtOrder * od.quantity) > od.amountPaid " +
            "ORDER BY od.id ASC")

    List<OrderDetail> findUnpaidOrderDetails(
            @Param("sessionId") Long sessionId,
            @Param("menuItemId") Long menuItemId
    );
}
