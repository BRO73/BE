package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.BookingRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LLMService {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntentEntity {
        private Intent intent;
        private LocalDate bookingDate;
        private Long locationId;
        private BookingRequest bookingRequest;
        private List<String> missingFields; // lưu các field còn thiếu
    }

    public enum Intent {
        BOOK_TABLE,
        CHECK_AVAILABILITY,
        UNKNOWN
    }

    /**
     * Parse chuỗi tự nhiên -> intent + entity
     * Tự động phát hiện thiếu thông tin
     */
    public IntentEntity parseIntent(String message) {
        if (message == null || message.isBlank()) {
            return new IntentEntity(Intent.UNKNOWN, null, null, null, null);
        }

        String msg = message.toLowerCase();
        Intent intent = Intent.UNKNOWN;
        BookingRequest bookingRequest = new BookingRequest();
        List<String> missing = new ArrayList<>();
        LocalDate bookingDate = null;

        // -------------------
        // BOOK_TABLE
        // -------------------
        if (msg.contains("đặt bàn") || msg.contains("muốn đặt bàn")) {
            intent = Intent.BOOK_TABLE;

            // 1️⃣ Parse thời gian tự nhiên
            LocalDateTime bookingDateTime = parseRelativeDateTime(msg);
            bookingRequest.setBookingTime(bookingDateTime);
            bookingDate = bookingDateTime != null ? bookingDateTime.toLocalDate() : null;
            if (bookingDateTime == null) missing.add("bookingTime");

            // 2️⃣ Parse số bàn nếu khách nói "bàn 1, bàn 2"
            List<Long> tableIds = extractTableIds(msg);
            bookingRequest.setTableIds(tableIds); // nếu trống, backend sẽ chọn
            if (tableIds.isEmpty() && msg.contains("bàn trống nào cũng được")) {
                // backend sẽ chọn bàn trống
            }

            // 3️⃣ Parse số khách
            Integer numGuests = extractNumGuests(msg);
            bookingRequest.setNumGuests(numGuests);
            if (numGuests == null) missing.add("numGuests");

            // 4️⃣ Parse tên
            String customerName = extractCustomerName(msg);
            bookingRequest.setCustomerName(customerName);
            if (customerName == null) missing.add("customerName");

            // 5️⃣ Parse số điện thoại
            String customerPhone = extractPhone(msg);
            bookingRequest.setCustomerPhone(customerPhone);
            if (customerPhone == null) missing.add("customerPhone");
        }

        // -------------------
        // CHECK_AVAILABILITY
        // -------------------
        else if (msg.contains("bàn trống") || msg.contains("còn bàn")) {
            intent = Intent.CHECK_AVAILABILITY;
            bookingDate = parseRelativeDate(msg);
            if (bookingDate == null) bookingDate = LocalDate.now();
        }

        return new IntentEntity(intent, bookingDate, null, bookingRequest, missing);
    }

    // ====================
    // Parse các entity
    // ====================

    private LocalDateTime parseRelativeDateTime(String msg) {
        try {
            // "14h 2 ngày sau" -> LocalDateTime
            Pattern p = Pattern.compile("(\\d{1,2})h(?:\\s*(\\d+)\\s*ngày sau)?");
            Matcher m = p.matcher(msg);
            if (m.find()) {
                int hour = Integer.parseInt(m.group(1));
                int daysAfter = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
                LocalDate date = LocalDate.now().plusDays(daysAfter);
                return date.atTime(hour, 0);
            }

            // fallback: yyyy-MM-dd hoặc yyyy-MM-dd HH:mm
            Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}(?:\\s\\d{2}:\\d{2})?");
            Matcher dm = datePattern.matcher(msg);
            if (dm.find()) {
                String str = dm.group();
                if (str.length() == 10) {
                    return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                } else {
                    return LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private LocalDate parseRelativeDate(String msg) {
        try {
            // "ngày mai", "2 ngày sau"
            if (msg.contains("ngày mai")) return LocalDate.now().plusDays(1);

            Pattern p = Pattern.compile("(\\d+)\\s*ngày sau");
            Matcher m = p.matcher(msg);
            if (m.find()) {
                int daysAfter = Integer.parseInt(m.group(1));
                return LocalDate.now().plusDays(daysAfter);
            }

            // fallback: yyyy-MM-dd
            Pattern datePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
            Matcher dm = datePattern.matcher(msg);
            if (dm.find()) {
                return LocalDate.parse(dm.group(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
        } catch (Exception e) {
        }
        return null;
    }

    private List<Long> extractTableIds(String msg) {
        List<Long> result = new ArrayList<>();
        try {
            Matcher m = Pattern.compile("bàn\\s*(\\d+)").matcher(msg);
            while (m.find()) result.add(Long.parseLong(m.group(1)));
        } catch (Exception e) {}
        return result;
    }

    private Integer extractNumGuests(String msg) {
        try {
            Matcher m = Pattern.compile("(\\d+)\\s*(người|khách)").matcher(msg);
            if (m.find()) return Integer.parseInt(m.group(1));
        } catch (Exception e) {}
        return null;
    }

    private String extractCustomerName(String msg) {
        // lấy tên sau "tên là ..." hoặc "tên:"
        Pattern p = Pattern.compile("tên\\s*(?:là|:)\\s*(\\p{L}+)", Pattern.UNICODE_CASE);
        Matcher m = p.matcher(msg);
        if (m.find()) return capitalize(m.group(1));
        return null;
    }

    private String extractPhone(String msg) {
        try {
            Matcher m = Pattern.compile("(\\d{9,11})").matcher(msg);
            if (m.find()) return m.group(1);
        } catch (Exception e) {}
        return null;
    }

    private String capitalize(String str) {
        if (str == null || str.isBlank()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
