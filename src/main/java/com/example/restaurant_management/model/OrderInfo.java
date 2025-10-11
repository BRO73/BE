package com.example.restaurant_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderInfo {
    private Long id;
    private String tableNumber;
    private String customerName;
    private List<MenuItem> items;
    private double totalAmount;
    private String time;
    private String status; // "Đang chế biến" | "Hoàn thành"
}
