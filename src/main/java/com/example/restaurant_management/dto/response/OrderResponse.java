package com.example.restaurant_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private LocalDateTime orderTime;
    private String status;
    private BigDecimal totalAmount;
    private String note;
    private TableResponse table;
    private StaffResponse staff;
    private List<OrderDetailResponse> items;
    private Long customerUserId;
    private String customerName;
    private String customerPhone;
}


