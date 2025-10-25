package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    // TransactionRepository
    boolean existsByBill(Bill bill);
}
