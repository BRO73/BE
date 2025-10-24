package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.StartSessionRequest;
import com.example.restaurant_management.entity.TableEntity;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.service.OrderSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order-sessions")
public class OrderSessionController {

    private final OrderSessionService orderSessionService;

    @Autowired
    public OrderSessionController(OrderSessionService orderSessionService) {
        this.orderSessionService = orderSessionService;
    }

    /**
     * Đây chính là API "CHECK-IN"
     * POST /api/order-sessions/start
     */
    @PostMapping("")
    public ResponseEntity<TableEntity> startOrJoinSession(@RequestBody StartSessionRequest request,
                                                          Authentication authentication) {
        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();
        Long userId = credentialPayload.getUserId();

        // 2. Gọi Service để xử lý nghiệp vụ
        // Parse tableId từ String sang Long
        Long tableIdLong = Long.parseLong(request.getTableId());

        TableEntity updatedTable = orderSessionService.startOrJoinSession(tableIdLong, userId);

        // 3. Trả về thông tin Bàn (TableEntity) đã được cập nhật
        // React sẽ nhận được cái này và setTableInfo(data)
        return ResponseEntity.ok(updatedTable);
    }
}