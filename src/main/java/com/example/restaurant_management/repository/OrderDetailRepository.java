package com.example.restaurant_management.repository;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.entity.OrderDetail;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);

    List<OrderDetail> findByMenuItemId(Long menuItemId);

    // Nên dùng enum cho đúng kiểu cột status
    List<OrderDetail> findByStatus(OrderItemStatus status);

    // (tuỳ chọn) nhiều trạng thái
    List<OrderDetail> findAllByStatusIn(List<OrderItemStatus> statuses);

    // Nếu nơi khác vẫn còn dùng String, tạm giữ lại (có thể xoá khi refactor xong)
    @Deprecated
    List<OrderDetail> findByStatus(String status);

    // Dùng để tránh race-condition khi “tách 1 đơn vị / giao 1 đơn vị”
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select od from OrderDetail od where od.id = :id")
    Optional<OrderDetail> findByIdForUpdate(@Param("id") Long id);
}
