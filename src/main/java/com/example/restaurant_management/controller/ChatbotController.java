package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.ChatbotRequest;
import com.example.restaurant_management.dto.response.ChatbotResponse;
import com.example.restaurant_management.service.ChatbotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@CrossOrigin(origins = "*")
public class ChatbotController {

    @Autowired
private ChatbotService chatbotService;

    @PostMapping
    public ChatbotResponse chat(@Valid @RequestBody ChatbotRequest request) {
        ChatbotResponse reply = chatbotService.chat(request);
        return reply;
    }
}
