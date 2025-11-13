package com.example.restaurant_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatbotRequest {
    private String message;             // câu hỏi/trò chuyện từ khách
    private String clientId;            // UUID do frontend sinh, dùng để lưu lịch sử
    private BookingRequest bookingRequest; // nếu khách nhập đủ info để đặt bàn
    private Long locationId;            // khu vực muốn đặt
    private String bookingDate;         // yyyy-MM-dd, dùng để check bàn trống
}
