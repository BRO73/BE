package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.request.ChatbotRequest;
import com.example.restaurant_management.dto.request.BookingRequest;
import com.example.restaurant_management.dto.response.BookingResponse;
import com.example.restaurant_management.dto.response.ChatbotResponse;
import com.example.restaurant_management.dto.response.TableResponse;
import com.example.restaurant_management.service.BookingService;
import com.example.restaurant_management.service.ChatHistoryService;
import com.example.restaurant_management.service.ChatbotService;
import com.example.restaurant_management.service.TableService;
import com.example.restaurant_management.util.GeminiService;
import com.example.restaurant_management.service.LLMService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final TableService tableService;
    private final BookingService bookingService;
    private final ChatHistoryService chatHistoryService;
    private final GeminiService geminiService;
    private final LLMService llmService;

    private static final Map<String, String> FRIENDLY_FIELD_NAME = new HashMap<>() {{
        put("customerName", "tên của bạn");
        put("customerPhone", "số điện thoại");
        put("bookingTime", "thời gian đặt bàn");
        put("numGuests", "số lượng khách");
        put("customerEmail", "email của bạn");
    }};

    @Override
    public ChatbotResponse chat(ChatbotRequest request) {
        String clientId = request.getClientId() != null ? request.getClientId() : "guest";

        // 1️⃣ Lưu message từ khách
        chatHistoryService.addMessage(clientId, "user", request.getMessage());

        // 2️⃣ Parse intent & entity bằng LLMService
        LLMService.IntentEntity intentEntity = llmService.parseIntent(request.getMessage());

        String reply;

        if (intentEntity.getIntent() == LLMService.Intent.BOOK_TABLE) {
            BookingRequest bookingRequest = intentEntity.getBookingRequest();

            // Nếu thiếu thông tin → yêu cầu bổ sung với tên thân thiện
            if (intentEntity.getMissingFields() != null && !intentEntity.getMissingFields().isEmpty()) {
                List<String> friendlyMissing = new ArrayList<>();
                for (String f : intentEntity.getMissingFields()) {
                    friendlyMissing.add(FRIENDLY_FIELD_NAME.getOrDefault(f, f));
                }
                reply = "Bạn vui lòng cung cấp thêm: " + String.join(", ", friendlyMissing);
            } else {
                // Nếu khách không chọn bàn → backend tự chọn bàn trống đầu tiên
                if (bookingRequest.getTableIds() == null || bookingRequest.getTableIds().isEmpty()) {
                    List<TableResponse> allTables = tableService.getAllTables();
                    List<Long> availableTables = new ArrayList<>();
                    LocalDate bookingDate = bookingRequest.getBookingTime().toLocalDate();

                    for (TableResponse table : allTables) {
                        boolean booked = bookingService.getAllBookings().stream()
                                .filter(b -> b.getBookingTime().toLocalDate().equals(bookingDate))
                                .anyMatch(b -> b.getStatus().equalsIgnoreCase("Confirmed") &&
                                        b.getTable().stream().anyMatch(t -> t.getId().equals(table.getId())));
                        if (!booked) {
                            availableTables.add(table.getId());
                            break; // chỉ chọn 1 bàn nếu khách không chọn
                        }
                    }
                    bookingRequest.setTableIds(availableTables);
                }

                // Nếu chưa có email, gán giá trị mặc định để tránh lỗi SQL
                if (bookingRequest.getCustomerEmail() == null || bookingRequest.getCustomerEmail().isBlank()) {
                    bookingRequest.setCustomerEmail("guest@example.com");
                }

                // Tạo booking
                reply = handleBookingRequest(bookingRequest, intentEntity.getLocationId());
            }
        } else if (intentEntity.getIntent() == LLMService.Intent.CHECK_AVAILABILITY) {
            // Kiểm tra bàn trống
            reply = handleTableAvailability(intentEntity.getBookingDate(), intentEntity.getLocationId());
        } else {
            // Nếu không hiểu → gọi Gemini trả lời thân thiện
            reply = geminiService.getChatResponse(request);
        }

        // 3️⃣ Lưu reply của bot
        chatHistoryService.addMessage(clientId, "bot", reply);

        return new ChatbotResponse(reply);
    }

    private String handleBookingRequest(BookingRequest bookingRequest, Long locationId) {
        if (bookingRequest == null ||
                bookingRequest.getCustomerName() == null ||
                bookingRequest.getCustomerPhone() == null ||
                bookingRequest.getBookingTime() == null ||
                bookingRequest.getTableIds() == null || bookingRequest.getTableIds().isEmpty()) {
            return "Để đặt bàn, vui lòng cung cấp đầy đủ thông tin: tên, số điện thoại, thời gian và bàn muốn đặt.";
        }

        LocalDate bookingDate = bookingRequest.getBookingTime().toLocalDate();
        List<Long> requestedTableIds = bookingRequest.getTableIds();

        List<TableResponse> allTables = tableService.getAllTables();
        List<Long> unavailableTables = new ArrayList<>();

        for (Long tableId : requestedTableIds) {
            boolean booked = bookingService.getAllBookings().stream()
                    .filter(b -> b.getBookingTime().toLocalDate().equals(bookingDate))
                    .anyMatch(b -> b.getStatus().equalsIgnoreCase("Confirmed") &&
                            b.getTable().stream().anyMatch(t -> t.getId().equals(tableId)));
            if (booked) unavailableTables.add(tableId);
        }

        if (!unavailableTables.isEmpty()) {
            return "Các bàn sau đã có booking vào ngày " + bookingDate + ": " +
                    unavailableTables.stream().map(String::valueOf).toList();
        }

        try {
            BookingResponse saved = bookingService.createBooking(bookingRequest);
            return "Đã đặt thành công bàn " + requestedTableIds +
                    " vào ngày " + bookingDate +
                    " cho " + bookingRequest.getCustomerName();
        } catch (Exception e) {
            return "Lỗi khi tạo booking: " + e.getMessage();
        }
    }

    private String handleTableAvailability(LocalDate bookingDate, Long locationId) {
        List<TableResponse> allTables = tableService.getAllTables();
        List<String> availableTables = new ArrayList<>();

        for (TableResponse table : allTables) {
            if (locationId != null && !table.getLocationId().equals(locationId)) continue;

            boolean isAvailable = bookingService.getAllBookings().stream()
                    .filter(b -> b.getBookingTime().toLocalDate().equals(bookingDate))
                    .noneMatch(b -> b.getStatus().equalsIgnoreCase("Confirmed") &&
                            b.getTable().stream().anyMatch(t -> t.getId().equals(table.getId())));

            if (isAvailable) {
                availableTables.add("Bàn " + table.getTableNumber() + " (" + table.getCapacity() + " chỗ)");
            }
        }

        return availableTables.isEmpty()
                ? "Không còn bàn trống vào ngày " + bookingDate
                : "Các bàn trống vào ngày " + bookingDate + ": " + String.join(", ", availableTables);
    }
}
