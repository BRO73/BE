package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.ChatMessage;

import java.util.List;

public interface ChatHistoryService {
    void addMessage(String clientId, String role, String message);
    List<ChatMessage> getHistory(String clientId);
    void clearHistory(String clientId);
}
