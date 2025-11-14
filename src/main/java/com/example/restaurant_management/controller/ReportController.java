package com.example.restaurant_management.controller;

import com.example.restaurant_management.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/daily")
    public Map<String, Object> getDailyReport(
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        LocalDateTime startDate = LocalDateTime.parse(start);
        LocalDateTime endDate = LocalDateTime.parse(end);
        return reportService.getDailyReport(startDate, endDate);
    }

    @GetMapping("/top-items")
    public Map<String, Object> getTopItems() {
        return reportService.getTopItems();
    }

    @GetMapping("/peak-hours1")
    public List<Map<String, Object>> getPeakHours() {
        return reportService.getPeakHours12();
    }

    @GetMapping("/last-7-days/revenue")
    public List<Map<String, Object>> getRevenueLast7Days() {
        return reportService.getRevenueLast7Days();
    }

    /** ✅ Top 5 món theo doanh thu trong 7 ngày */
    @GetMapping("/last-7-days/top-items")
    public List<Map<String, Object>> getTopItemsLast7Days() {
        return reportService.getTopItemsLast7Days();
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummaryReport(
            @RequestParam(defaultValue = "7") int days
    ) {
        return reportService.getSummaryReport(days);
    }

    @GetMapping("/peak-hours")
    public List<Map<String, Object>> getPeakHours(
            @RequestParam(defaultValue = "7") int days
    ) {
        return reportService.getPeakHours(days);
    }

    @GetMapping("/top-customers")
    public List<Map<String, Object>> getTopCustomers(
            @RequestParam(defaultValue = "7") int days
    ) {
        return reportService.getTopCustomers(days);
    }

}
