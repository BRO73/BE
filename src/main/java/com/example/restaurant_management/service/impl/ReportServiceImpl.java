package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.Transaction;
import com.example.restaurant_management.repository.OrderRepository;
import com.example.restaurant_management.repository.TransactionRepository;
import com.example.restaurant_management.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

    public ReportServiceImpl(TransactionRepository transactionRepository, OrderRepository orderRepository) {
        this.transactionRepository = transactionRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Map<String, Object> getDailyReport(LocalDateTime start, LocalDateTime end) {

        Double totalRevenue = transactionRepository.sumTotalAmountBetween(start, end);
        Long totalOrders = transactionRepository.countOrdersBetween(start, end);
        Double avgOrderValue = transactionRepository.avgOrderValueBetween(start, end);
        Long customerVisits = transactionRepository.countDistinctCustomerBetween(start, end);

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

        List<Object[]> rows = transactionRepository.revenueByDayBetween(start, end);

        return rows.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("day", r[0]);
            m.put("revenue", ((Number) r[1]).doubleValue());
            m.put("orders", ((Number) r[2]).longValue());
            return m;
        }).toList();
    }


    @Override
    public List<Map<String, Object>> getTopItemsLast7Days() {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(7);

        List<Object[]> rows = transactionRepository.topItemsRevenueBetween(start, end);

        List<Map<String, Object>> result = new ArrayList<>();
        int index = 1;
        for (Object[] r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", index++);
            m.put("name", (String) r[0]);
            m.put("orders", ((Number) r[1]).intValue());
            m.put("revenue", ((Number) r[2]).doubleValue());
            result.add(m);
        }

        return result;
    }


    @Override
    public Map<String, Object> getSummaryReport(int days) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(days);

        List<Transaction> transactions =
                transactionRepository.findByPaymentStatusAndTransactionTimeBetween(
                        "SUCCESS", startDate, endDate
                );

        BigDecimal totalRevenue = transactions.stream()
                .map(Transaction::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = transactions.size();

        BigDecimal avgOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long customerVisits = transactions.stream()
                .map(t -> t.getOrder().getCustomerUser())
                .filter(Objects::nonNull)
                .map(u -> u.getId())
                .distinct()
                .count();

        Map<String, Object> map = new HashMap<>();
        map.put("totalRevenue", totalRevenue);
        map.put("totalOrders", totalOrders);
        map.put("avgOrderValue", avgOrderValue);
        map.put("customerVisits", customerVisits);
        map.put("days", days);

        return map;
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
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days);

        List<Transaction> transactions =
                transactionRepository.findByPaymentStatusAndTransactionTimeBetween(
                        "PAID", start, end
                );

        Map<String, BigDecimal> customerRevenue = new HashMap<>();
        Map<String, Long> visitCount = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getOrder().getCustomerUser() != null) {
                String name = t.getOrder().getCustomerUser().getUsername();

                customerRevenue.merge(name, t.getAmountPaid(), BigDecimal::add);
                visitCount.merge(name, 1L, Long::sum);
            }
        }

        return customerRevenue.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("name", e.getKey());
                    m.put("revenue", e.getValue());
                    m.put("visitCount", visitCount.get(e.getKey()));
                    return m;
                })
                .toList();
    }


    @Override
    public List<Map<String, Object>> getRevenueByDays(int days) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days - 1)
                .withHour(0).withMinute(0).withSecond(0).withNano(0);

        List<Object[]> rows = transactionRepository.revenueByDayBetween(start, end);

        return rows.stream().map(r -> {
            Map<String, Object> m = new HashMap<>();
            m.put("day", r[0]);
            m.put("revenue", ((Number) r[1]).doubleValue());
            m.put("orders", ((Number) r[2]).longValue());
            return m;
        }).toList();
    }

    @Override
    public List<Map<String, Object>> getTopItemsByDays(int days) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusDays(days);

        List<Object[]> rows = transactionRepository.topItemsByDays(start, end);

        List<Map<String, Object>> result = new ArrayList<>();
        int index = 1;
        for (Object[] r : rows) {
            Map<String, Object> m = new HashMap<>();
            m.put("id", index++);
            m.put("name", (String) r[0]);
            m.put("orders", ((Number) r[1]).intValue());
            m.put("revenue", ((Number) r[2]).doubleValue());
            result.add(m);
        }

        return result;
    }


}
