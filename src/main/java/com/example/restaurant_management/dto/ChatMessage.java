package com.example.restaurant_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String role; // "user" hoặc "bot"
    private String message;
}
