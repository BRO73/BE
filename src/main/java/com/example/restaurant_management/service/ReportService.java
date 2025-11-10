package com.example.restaurant_management.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {

    Map<String, Object> getDailyReport(LocalDateTime start, LocalDateTime end);

    Map<String, Object> getTopItems();

    List<Map<String, Object>> getPeakHours12();

    List<Map<String, Object>> getRevenueLast7Days();

    List<Map<String, Object>> getTopItemsLast7Days();

    Map<String, Object> getSummaryReport(int days);

    List<Map<String, Object>> getPeakHours(int days);

    List<Map<String, Object>> getTopCustomers(int days);
}
