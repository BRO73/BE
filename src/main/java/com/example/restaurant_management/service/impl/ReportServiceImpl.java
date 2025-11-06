package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.repository.OrderRepository;
import com.example.restaurant_management.service.ReportService;
import org.springframework.stereotype.Service;

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
    public List<Map<String, Object>> getPeakHours() {
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

}
