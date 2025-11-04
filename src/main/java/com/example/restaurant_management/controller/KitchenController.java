package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.UpdateOrderDetailStatusRequest;
import com.example.restaurant_management.dto.response.KitchenBoardResponse;
import com.example.restaurant_management.service.KitchenService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/kitchen")
@RequiredArgsConstructor
public class KitchenController {

    private final KitchenService kitchenService;

    @PermitAll
    @GetMapping("/board")
    public ResponseEntity<KitchenBoardResponse> getBoard(
            @RequestParam(name = "limit", defaultValue = "50") int limit,
            @RequestParam(name = "storeName", required = false) String storeName
    ) {
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

    /** Cũ: /api/kitchen/order-details/{id}/status */
    @PatchMapping("/order-details/{id}/status")
    public ResponseEntity<Void> updateOrderItemStatusByOrderDetailsPath(
            @PathVariable Long id,
            @RequestBody UpdateOrderDetailStatusRequest req
    ) {
        kitchenService.updateOrderDetailStatus(id, req.getStatus());
        return ResponseEntity.ok().build();
    }

    /** FE cũng gọi phẳng: /api/kitchen/{id}/status */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateOrderItemStatusFlatPath(
            @PathVariable Long id,
            @RequestBody UpdateOrderDetailStatusRequest req
    ) {
        kitchenService.updateOrderDetailStatus(id, req.getStatus());
        return ResponseEntity.ok().build();
    }

    /** NEW: “>” — hoàn thành 1 đơn vị */
    @PatchMapping("/order-details/{id}/complete-one")
    public ResponseEntity<Void> completeOneUnit(@PathVariable Long id) {
        kitchenService.completeOneUnit(id);
        return ResponseEntity.ok().build();
    }

    /** NEW: (panel chờ cung ứng) xuất 1 đơn vị */
    @PatchMapping("/order-details/{id}/serve-one")
    public ResponseEntity<Void> serveOneUnit(@PathVariable Long id) {
        kitchenService.serveOneUnit(id);
        return ResponseEntity.ok().build();
    }
}
