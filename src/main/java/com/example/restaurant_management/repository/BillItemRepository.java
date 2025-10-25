package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Bill;
import com.example.restaurant_management.entity.BillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillItemRepository extends JpaRepository<BillItem, Long> {
    List<BillItem> findByBill(Bill bill);
}
