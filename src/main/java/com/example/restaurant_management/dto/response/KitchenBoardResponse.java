package com.example.restaurant_management.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class KitchenBoardResponse {
    private LocalDateTime serverTime;
    private Integer overtimeMinutes;
    private List<KitchenTicketResponse> pending;
    private List<KitchenTicketResponse> inProgress;
    private List<KitchenTicketResponse> ready;
}
