package com.example.restaurant_management.repository;

import com.example.restaurant_management.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByTableId(Long tableId);
    List<Order> findByStatus(String status);
    List<Order> findByStaffUserId(Long userId);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 🔹 Tổng doanh thu trong khoảng thời gian
    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Double sumTotalAmountBetween(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    // 🔹 Số lượng order
    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countOrdersBetween(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    // 🔹 Giá trị trung bình mỗi đơn
    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Double avgOrderValueBetween(@Param("start") LocalDateTime start,
                                @Param("end") LocalDateTime end);

    // 🔹 Số khách duy nhất
    @Query("SELECT COUNT(DISTINCT o.customerUser.id) FROM Order o WHERE o.createdAt BETWEEN :start AND :end")
    Long countDistinctCustomerBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    // 🔹 Top 5 món bán chạy nhất
    @Query("SELECT m.name, COUNT(od.id) AS orders, SUM(od.priceAtOrder * od.quantity) AS revenue " +
            "FROM OrderDetail od JOIN od.menuItem m " +
            "GROUP BY m.name " +
            "ORDER BY orders DESC")
    List<Object[]> findTopItems();

    // 🔹 Giờ cao điểm (sử dụng HOUR của MySQL)
    @Query("SELECT FUNCTION('HOUR', o.createdAt) AS hour, COUNT(o.id) AS orders " +
            "FROM Order o " +
            "GROUP BY FUNCTION('HOUR', o.createdAt) " +
            "ORDER BY FUNCTION('HOUR', o.createdAt)")
    List<Object[]> findPeakHours();

    /** Doanh thu theo ngày trong 7 ngày gần nhất */
    @Query(value = """
        SELECT DATE(o.created_at) AS day,
               COALESCE(SUM(o.total_amount), 0) AS revenue,
               COUNT(o.id) AS orders
        FROM orders o
        WHERE o.created_at BETWEEN :start AND :end
        GROUP BY DATE(o.created_at)
        ORDER BY day
        """, nativeQuery = true)
    List<Object[]> revenueByDayBetween(@Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);


    /** Top 5 món theo doanh thu trong 7 ngày gần nhất */
    @Query(value = """
        SELECT mi.name AS item_name,
               SUM(od.quantity) AS total_qty,
               SUM(od.price_at_order * od.quantity) AS revenue
        FROM order_details od
        JOIN menu_items mi ON mi.id = od.menu_item_id
        JOIN orders o      ON o.id  = od.order_id
        WHERE o.created_at BETWEEN :start AND :end
        GROUP BY mi.name
        ORDER BY revenue DESC
        LIMIT 5
        """, nativeQuery = true)
    List<Object[]> topItemsRevenueBetween(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

}
