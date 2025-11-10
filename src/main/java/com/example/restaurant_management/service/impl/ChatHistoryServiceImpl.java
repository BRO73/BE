package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.ChatMessage;
import com.example.restaurant_management.service.ChatHistoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {

    private final ConcurrentHashMap<String, List<ChatMessage>> historyMap = new ConcurrentHashMap<>();

    @Override
    public void addMessage(String clientId, String role, String message) {
        historyMap.computeIfAbsent(clientId, k -> new ArrayList<>())
                  .add(new ChatMessage(role, message));
    }

    @Override
    public List<ChatMessage> getHistory(String clientId) {
        return historyMap.getOrDefault(clientId, new ArrayList<>());
    }

    @Override
    public void clearHistory(String clientId) {
        historyMap.remove(clientId);
    }
}
