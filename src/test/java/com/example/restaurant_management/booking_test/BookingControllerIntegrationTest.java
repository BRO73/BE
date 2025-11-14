package com.example.restaurant_management.booking_test;

import com.example.restaurant_management.dto.request.BookingRequest;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.repository.TableRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired TableRepository tableRepository;

    @BeforeEach
    void setup() {
        tableRepository.deleteAll();
        TableEntity t = TableEntity.builder()
                .tableNumber("T-100")
                .capacity(4)
                .status("Available")
                .build();
        tableRepository.save(t);
    }

    @Test
    void postCreateBooking_returns201_and_responseContainsCustomerName() throws Exception {
        TableEntity t = tableRepository.findAll().get(0);

        BookingRequest req = BookingRequest.builder()
                .tableIds(List.of(t.getId()))
                .customerName("Minh")
                .customerPhone("0912345678")
                .bookingTime(LocalDateTime.now().plusDays(1))
                .numGuests(2)
                .build();

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerName").value("Minh"));
    }
}
