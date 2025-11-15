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
}
