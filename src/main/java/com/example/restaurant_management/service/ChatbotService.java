package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.request.ChatbotRequest;
import com.example.restaurant_management.dto.response.ChatbotResponse;

public interface ChatbotService {
    ChatbotResponse chat(ChatbotRequest request);
}
