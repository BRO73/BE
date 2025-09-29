package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.response.KitchenBoardResponse;
import com.example.restaurant_management.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;


    @GetMapping
    public ResponseEntity<KitchenBoardResponse> board(@RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(kitchenService.board(limit));
    }


    @PatchMapping("/{orderDetailId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable Long orderDetailId,
            @RequestBody Map<String, String> body
    ) {
        String next = body.get("status");
        kitchenService.updateStatus(orderDetailId, next);
        return ResponseEntity.ok(Map.of("ok", true, "orderDetailId", orderDetailId, "status", next));
    }
}
