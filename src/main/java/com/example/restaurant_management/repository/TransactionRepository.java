package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByOrderId(Long orderId);
    Optional<Transaction> findByTransactionCode(String transactionCode);
    List<Transaction> findByPaymentMethod(String paymentMethod);
    List<Transaction> findByCashierUserId(Long cashierId);
    List<Transaction> findByTransactionTimeBetween(LocalDateTime start, LocalDateTime end);
    List<Transaction> findByOrder_Id(Long orderId);
    Optional<Transaction> findByOrder_IdAndPaymentStatus(Long orderId, String status);
    List<Transaction> findByPaymentStatusAndTransactionTimeBetween(
            String status,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Transaction t " +
            "WHERE t.promotionId = :promotionId " +
            "  AND t.order.customerUser.id = :customerUserId " +
            "  AND t.paymentStatus = :paymentStatus")
    boolean existsByPromotionIdAndOrderCustomerUserIdAndPaymentStatus(
            @Param("promotionId") Long promotionId,
            @Param("customerUserId") Long customerUserId,
            @Param("paymentStatus") String paymentStatus
    );

    // Tổng doanh thu
    @Query("SELECT SUM(t.amountPaid) FROM Transaction t " +
            "WHERE t.transactionTime BETWEEN :start AND :end " +
            "AND t.paymentStatus = 'PAID'")
    Double sumTotalAmountBetween(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    // Số đơn thanh toán thành công
    @Query("SELECT COUNT(t) FROM Transaction t " +
            "WHERE t.transactionTime BETWEEN :start AND :end " +
            "AND t.paymentStatus = 'PAID'")
    Long countOrdersBetween(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    // Giá trị trung bình mỗi giao dịch (AOV)
    @Query("SELECT AVG(t.amountPaid) FROM Transaction t " +
            "WHERE t.transactionTime BETWEEN :start AND :end " +
            "AND t.paymentStatus = 'PAID'")
    Double avgOrderValueBetween(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    // Đếm khách hàng unique
    @Query("SELECT COUNT(DISTINCT t.order.customerUser.id) " +
            "FROM Transaction t WHERE t.transactionTime BETWEEN :start AND :end " +
            "AND t.paymentStatus = 'PAID'")
    Long countDistinctCustomerBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    /** Doanh thu theo ngày */
    @Query(value = """
        SELECT DATE(t.transaction_time) AS day,
               SUM(t.amount_paid) AS revenue,
               COUNT(t.id) AS orders
        FROM transactions t
        WHERE t.transaction_time BETWEEN :start AND :end
        AND t.payment_status = 'PAID'
        GROUP BY DATE(t.transaction_time)
        ORDER BY day
        """, nativeQuery = true)
    List<Object[]> revenueByDayBetween(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);


    /** Top món bán chạy theo transaction → join order_details */
        @Query(value = """
        SELECT 
            mi.name AS item_name,
            SUM(od.quantity) AS total_orders,
            SUM(od.price_at_order * od.quantity) AS revenue
        FROM order_details od
        JOIN menu_items mi ON mi.id = od.menu_item_id
        JOIN orders o ON o.id = od.order_id
        JOIN transactions t ON t.order_id = o.id
        WHERE t.transaction_time BETWEEN :start AND :end
          AND t.payment_status = 'PAID'
        GROUP BY mi.name
        ORDER BY revenue DESC
        LIMIT 5
    """, nativeQuery = true)
        List<Object[]> topItemsRevenueBetween(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);


    @Query(value = """
    SELECT 
        mi.name AS item_name,
        SUM(od.quantity) AS total_orders,
        SUM(od.price_at_order * od.quantity) AS revenue
    FROM order_details od
    JOIN menu_items mi ON mi.id = od.menu_item_id
    JOIN orders o ON o.id = od.order_id
    JOIN transactions t ON t.order_id = o.id
    WHERE t.transaction_time BETWEEN :start AND :end
      AND t.payment_status = 'PAID'
    GROUP BY mi.name
    ORDER BY revenue DESC
""", nativeQuery = true)
    List<Object[]> topItemsByDays(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

}
