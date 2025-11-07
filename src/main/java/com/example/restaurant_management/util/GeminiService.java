package com.example.restaurant_management.util;

import com.example.restaurant_management.config.GeminiConfig;
import com.example.restaurant_management.dto.request.ChatbotRequest;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class GeminiService {

    @Autowired
    private GeminiConfig config;

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s";

    public String getChatResponse(ChatbotRequest request) {
        try {
            String url = String.format(API_URL, config.getModel(), config.getApiKey());

            // 🧠 Thêm system instruction
            Map<String, Object> body = Map.of(
                    "system_instruction", Map.of(
                            "parts", new Object[]{
                                    Map.of("text", "Bạn là chatbot của nhà hàng, trả lời thật ngắn gọn trong khoảng 30–50 từ, rõ ràng và thân thiện.")
                            }
                    ),
                    "contents", new Object[]{
                            Map.of("parts", new Object[]{
                                    Map.of("text", request.getMessage())
                            })
                    }
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

            JsonObject json = JsonParser.parseString(response.getBody()).getAsJsonObject();

            String reply = json.getAsJsonArray("candidates")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("content")
                    .getAsJsonArray("parts")
                    .get(0).getAsJsonObject()
                    .get("text").getAsString();

            return reply;
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, tôi không thể xử lý yêu cầu này vào lúc này.";
        }
    }
}
