package com.example.restaurant_management.repository;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.entity.OrderDetail;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);

    List<OrderDetail> findByMenuItemId(Long menuItemId);

    List<OrderDetail> findByStatus(OrderItemStatus status);

    List<OrderDetail> findAllByStatusIn(List<OrderItemStatus> statuses);

    @Deprecated
    List<OrderDetail> findByStatus(String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select od from OrderDetail od where od.id = :id")
    Optional<OrderDetail> findByIdForUpdate(@Param("id") Long id);

    // ===== CHẶT HƠN: Gộp về dòng gốc trong CÙNG order, loại trừ chính dòng rollback =====
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select od from OrderDetail od
        where od.order.id = :orderId
          and od.id <> :excludeId
          and od.menuItem.id = :menuItemId
          and od.status in :statuses
          and ((od.priceAtOrder is null and :priceAtOrder is null) or od.priceAtOrder = :priceAtOrder)
          and ((od.notes is null and :notes is null) or od.notes = :notes)
        order by od.createdAt asc
        """)
    List<OrderDetail> findMergeTargetsForRollback(
            @Param("orderId") Long orderId,
            @Param("excludeId") Long excludeId,
            @Param("menuItemId") Long menuItemId,
            @Param("statuses") List<String> statuses,
            @Param("priceAtOrder") BigDecimal priceAtOrder,
            @Param("notes") String notes
    );

    @Query("""
        SELECT od FROM OrderDetail od
        JOIN FETCH od.menuItem m
        JOIN FETCH od.order o
        LEFT JOIN FETCH o.table t      
        LEFT JOIN FETCH t.location        
        LEFT JOIN FETCH m.category   
        WHERE od.status IN :statuses
        """)
    List<OrderDetail> findAllByStatusInWithDetails(
            @Param("statuses") List<String> statuses
    );
}
