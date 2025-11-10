package com.example.restaurant_management.service;

import com.example.restaurant_management.dto.ChatMessage;
import com.example.restaurant_management.dto.request.BookingRequest;
import com.example.restaurant_management.util.GeminiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LLMService {

    private final GeminiService geminiService; // hoặc OpenAIService nếu bạn dùng GPT

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IntentEntity {
        private Intent intent;
        private LocalDate bookingDate;
        private Long locationId;
        private BookingRequest bookingRequest;
        private List<String> missingFields;
    }

    public enum Intent {
        BOOK_TABLE,
        CHECK_AVAILABILITY,
        UNKNOWN
    }

    public IntentEntity parseIntent(String message, List<ChatMessage> history) {
        LocalDate today = LocalDate.now();
        String todayStr = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        try {
            // 1️⃣ Chuyển history thành chuỗi
            StringBuilder historyStr = new StringBuilder();
            for (ChatMessage chat : history) {
                historyStr.append(chat.getRole()).append(": ").append(chat.getMessage()).append("\n");
            }

            // 2️⃣ Tạo prompt gửi tới Gemini
            String prompt = """
            Hôm nay là ngày %s.
            Bạn là hệ thống nhận dạng ý định khách hàng trong nhà hàng Riverside Terrace.
            Hãy đọc câu người dùng và lịch sử chat sau, trả về JSON hợp lệ (không bao gồm dấu ``` hoặc text ngoài JSON)
            theo đúng định dạng:

            {
                "intent": "BOOK_TABLE" | "CHECK_AVAILABILITY" | "UNKNOWN",
                "bookingRequest": {
                    "customerName": "...",
                    "customerPhone": "...",
                    "bookingTime": "yyyy-MM-ddTHH:mm:00",
                    "numGuests": ...,
                    "tableIds": [1,2],
                    "customerEmail": "..."
                },
                "missingFields": ["customerPhone", "bookingTime"]
            }

            Lịch sử chat:
            %s

            Câu người dùng:
            "%s"
        """.formatted(todayStr, historyStr.toString(), message);

            // 3️⃣ Gọi Gemini
            String aiResponse = geminiService.ask(prompt);

            // 4️⃣ Làm sạch và parse JSON (giống code cũ)
            String cleanResponse = aiResponse.trim().replaceAll("^```(json)?", "").replaceAll("```$", "").trim();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm"));

            JsonNode root = mapper.readTree(cleanResponse);

            Intent intent = Intent.valueOf(root.path("intent").asText("UNKNOWN").toUpperCase(Locale.ROOT));
            BookingRequest bookingRequest = root.has("bookingRequest") && !root.get("bookingRequest").isNull()
                    ? mapper.treeToValue(root.get("bookingRequest"), BookingRequest.class)
                    : null;

            List<String> missing = new ArrayList<>();
            if (root.has("missingFields")) {
                for (JsonNode f : root.get("missingFields")) missing.add(f.asText());
            }

            LocalDate bookingDate = bookingRequest != null && bookingRequest.getBookingTime() != null
                    ? bookingRequest.getBookingTime().toLocalDate()
                    : null;

            return new IntentEntity(intent, bookingDate, null, bookingRequest, missing);

        } catch (Exception e) {
            e.printStackTrace();
            return new IntentEntity(Intent.UNKNOWN, null, null, null, List.of());
        }
    }

}
