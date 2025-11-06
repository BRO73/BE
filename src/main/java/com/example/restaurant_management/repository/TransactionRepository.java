package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
