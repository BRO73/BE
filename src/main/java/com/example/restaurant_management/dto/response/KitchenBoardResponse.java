package com.example.restaurant_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenBoardResponse {
    private LocalDateTime serverTime;
    private List<KitchenTicketResponse> items;
}
