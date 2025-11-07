package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.repository.OrderRepository;
import com.example.restaurant_management.service.ReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;

    public ReportServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Map<String, Object> getDailyReport(LocalDateTime start, LocalDateTime end) {
        Double totalRevenue = orderRepository.sumTotalAmountBetween(start, end);
        Long totalOrders = orderRepository.countOrdersBetween(start, end);
        Double avgOrderValue = orderRepository.avgOrderValueBetween(start, end);
        Long customerVisits = orderRepository.countDistinctCustomerBetween(start, end);

        Map<String, Object> report = new HashMap<>();
        report.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        report.put("totalOrders", totalOrders != null ? totalOrders : 0);
        report.put("avgOrderValue", avgOrderValue != null ? avgOrderValue : 0.0);
        report.put("customerVisits", customerVisits != null ? customerVisits : 0);
        return report;
    }

    @Override
    public Map<String, Object> getTopItems() {
        List<Object[]> results = orderRepository.findTopItems();

        List<Map<String, Object>> topItems = results.stream()
                .map(obj -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", obj[0]);
                    item.put("orders", obj[1]);
                    item.put("revenue", obj[2]);
                    return item;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("topItems", topItems);
        return response;
    }

    @Override
    public List<Map<String, Object>> getPeakHours12() {
        List<Object[]> results = orderRepository.findPeakHours();

        return results.stream()
                .map(obj -> {
                    Map<String, Object> hourData = new HashMap<>();
                    hourData.put("hour", obj[0]);
                    hourData.put("orders", obj[1]);
                    return hourData;
                })
                .collect(Collectors.toList());
    }


    @Override
    public List<Map<String, Object>> getRevenueLast7Days() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<Object[]> rows = orderRepository.revenueByDayBetween(start, end);
        return rows.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("day", r[0]);               // java.sql.Date / String tùy driver
            m.put("revenue", ((Number) r[1]).doubleValue());
            m.put("orders",  ((Number) r[2]).longValue());
            return m;
        }).toList();
    }

    @Override
    public List<Map<String, Object>> getTopItemsLast7Days() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7);

        List<Object[]> rows = orderRepository.topItemsRevenueBetween(start, end);
        return rows.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("name",    (String) r[0]);
            m.put("orders",  ((Number) r[1]).longValue());
            m.put("revenue", ((Number) r[2]).doubleValue());
            return m;
        }).toList();
    }

    public Map<String, Object> getSummaryReport(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);

        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = orders.size();
        BigDecimal avgOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Số khách hàng (tính distinct theo customer_user_id)
        long customerVisits = orders.stream()
                .map(o -> o.getCustomerUser() != null ? o.getCustomerUser().getId() : null)
                .filter(Objects::nonNull)
                .distinct()
                .count();

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRevenue", totalRevenue);
        summary.put("totalOrders", totalOrders);
        summary.put("avgOrderValue", avgOrderValue);
        summary.put("customerVisits", customerVisits);
        summary.put("days", days);

        return summary;
    }

    @Override
    public List<Map<String, Object>> getPeakHours(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);

        Map<Integer, BigDecimal> revenueByHour = new HashMap<>();
        for (Order order : orders) {
            if (order.getCreatedAt() != null && order.getTotalAmount() != null) {
                int hour = order.getCreatedAt().getHour();
                revenueByHour.merge(hour, order.getTotalAmount(), BigDecimal::add);
            }
        }

        return revenueByHour.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hour", e.getKey());
                    map.put("revenue", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }


    // Top khách hàng chi tiêu cao nhất
    @Override
    public List<Map<String, Object>> getTopCustomers(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, endDate);

        Map<String, BigDecimal> customerRevenue = new HashMap<>();
        for (Order order : orders) {
            if (order.getCustomerUser() != null && order.getTotalAmount() != null) {
                String name = order.getCustomerUser().getUsername();
                customerRevenue.merge(name, order.getTotalAmount(), BigDecimal::add);
            }
        }

        return customerRevenue.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", e.getKey());
                    map.put("revenue", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }


}
