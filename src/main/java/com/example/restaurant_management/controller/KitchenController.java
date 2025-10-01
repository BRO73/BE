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

    @PatchMapping("/menu-items/{id}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Object avail = body.get("available");
        boolean available = (avail instanceof Boolean)
                ? (Boolean) avail
                : (avail instanceof String && Boolean.parseBoolean((String) avail));

        kitchenService.updateMenuAvailability(id, available);
        return ResponseEntity.ok().build();
    }
}
