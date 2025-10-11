package com.example.restaurant_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private Long id;
    private String name;
    private double price;
    private int quantity;
    private String status; // "Đang chờ bếp xác nhận", "Đang chế biến", "Hoàn thành"
}
