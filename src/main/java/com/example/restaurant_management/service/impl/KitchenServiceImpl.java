package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.response.KitchenBoardResponse;
import com.example.restaurant_management.dto.response.KitchenTicketResponse;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.repository.OrderDetailRepository;
import com.example.restaurant_management.service.KitchenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.time.Instant;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class KitchenServiceImpl implements KitchenService {

    private static final String TOPIC_BOARD = "/topic/kitchen/board";

    private final OrderDetailRepository orderDetailRepository;
    private final MenuItemRepository menuItemRepo;
    private final SimpMessagingTemplate simp;

    private int defaultLimit() {
        return 200;
    }

    private String norm(String s) {
        return s == null ? null : s.trim().toUpperCase();
    }


    /** Gửi snapshot board lên topic. */
    private void broadcastBoardSnapshot() {
        try {
            KitchenBoardResponse snapshot = board(defaultLimit());
            simp.convertAndSend(TOPIC_BOARD, snapshot);
            log.info("✅ Broadcasted board snapshot: {} items", snapshot.getItems().size());
            System.out.println("✅ [WS] Broadcasted to /topic/kitchen/board - Items: " + snapshot.getItems().size());
        } catch (Exception e) {
            log.error("❌ Failed to broadcast board snapshot", e);
            System.err.println("❌ [WS] Broadcast failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void notifyBoardUpdate() {
        log.info("🔔 Received external notification to update board, broadcasting...");
        System.out.println("🔔 [WS] Received notify from other service. Broadcasting...");
        broadcastBoardSnapshot(); // Gọi lại hàm private đã có
    }

    @Override
    @Transactional(readOnly = true)
    public KitchenBoardResponse board(int limit) {
        List<String> activeStatuses = List.of(
                "PENDING",
                "DONE"
                // COMPLETED không hiện trên bếp
        );

        List<OrderDetail> all = orderDetailRepository.findAllByStatusInWithDetails(activeStatuses);

        Comparator<OrderDetail> byCreated = Comparator.comparing(OrderDetail::getCreatedAt);

        List<KitchenTicketResponse> items = all.stream()
                .sorted(byCreated)
                .limit(limit)
                .map(KitchenTicketResponse::from)
                .toList();

        return KitchenBoardResponse.builder()
                .serverTime(Instant.now().toString())
                .items(items)
                .build();
    }

    @Override
    @Transactional
    public void updateMenuAvailability(Long menuItemId, boolean available) {
        com.example.restaurant_management.entity.MenuItem menu = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found: " + menuItemId));
        menu.setAvailability(available
                ? com.example.restaurant_management.common.enums.MenuItemAvailability.AVAILABLE
                : com.example.restaurant_management.common.enums.MenuItemAvailability.UNAVAILABLE);
        menu.setUpdatedAt(LocalDateTime.now());
        menuItemRepo.save(menu);

        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void updateOrderDetailStatus(Long orderDetailId, String status) {
        OrderDetail od = orderDetailRepository.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        String curRaw = od.getStatus();
        String cur = norm(curRaw);        // trạng thái hiện tại chuẩn hoá
        String statusNorm = norm(status); // trạng thái target chuẩn hoá

        log.info("🔄 Status transition: {} -> {} for orderDetailId={}", curRaw, statusNorm, orderDetailId);
        System.out.println("🔄 [STATUS] " + curRaw + " -> " + statusNorm + " (ID: " + orderDetailId + ")");

        switch (cur) {
            case "PENDING" -> {
                // PENDING -> DONE hoặc PENDING -> CANCELED
                if (!statusNorm.equals("DONE") && !statusNorm.equals("CANCELED")) {
                    throw new IllegalStateException("Invalid transition from PENDING to " + statusNorm);
                }
            }

            case "DONE" -> {
                // DONE -> PENDING (rollback) hoặc DONE -> COMPLETED
                if (!statusNorm.equals("PENDING") && !statusNorm.equals("COMPLETED")) {
                    throw new IllegalStateException("Invalid transition from DONE to " + statusNorm);
                }

                if (statusNorm.equals("PENDING")) {
                    // ✅ ROLLBACK: DONE -> PENDING
                    od.setStatus("PENDING");
                    od.setUpdatedAt(LocalDateTime.now());
                    orderDetailRepository.save(od);
                    log.info("✅ Rollback DONE -> PENDING for orderDetailId={}", orderDetailId);
                    System.out.println("✅ [ROLLBACK] DONE -> PENDING (ID: " + orderDetailId + ")");
                    broadcastBoardSnapshot();
                    return;
                }

                if (statusNorm.equals("COMPLETED")) {
                    // ✅ HOÀN TẤT: DONE -> COMPLETED
                    od.setStatus("COMPLETED");
                    od.setUpdatedAt(LocalDateTime.now());
                    orderDetailRepository.save(od);
                    log.info("✅ DONE -> COMPLETED for orderDetailId={}", orderDetailId);
                    System.out.println("✅ [COMPLETE] DONE -> COMPLETED (ID: " + orderDetailId + ")");
                    broadcastBoardSnapshot();
                    return;
                }
            }

            case "COMPLETED", "CANCELED" -> {
                throw new IllegalStateException("Item already finished. Current status=" + cur);
            }

            default -> throw new IllegalStateException("Unknown status: " + cur);
        }

        // Các case còn lại (PENDING -> DONE/CANCELED)
        od.setStatus(statusNorm);
        od.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(od);

        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void completeOneUnit(Long orderDetailId) {
        OrderDetail src = orderDetailRepository.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        String cur = norm(src.getStatus());
        System.out.println(">>> [COMPLETE_ONE] orderDetailId=" + orderDetailId + ", status=" + cur);

        // Không cho complete nếu đã CANCELED hoặc COMPLETED
        if (cur.equals("CANCELED") || cur.equals("COMPLETED")) {
            throw new IllegalStateException("Invalid state to complete-one: " + cur);
        }

        if (cur.equals("DONE")) {
            System.out.println(">>> [COMPLETE_ONE] Already DONE, broadcasting...");
            broadcastBoardSnapshot();
            return;
        }

        int qty = src.getQuantity();
        if (qty <= 1) {
            src.setStatus("DONE"); // setter/DB có thể lưu sao cũng được, logic dùng norm
            src.setUpdatedAt(LocalDateTime.now());
            orderDetailRepository.save(src);
            System.out.println("✅ [COMPLETE_ONE] Set to DONE (qty was 1)");
            broadcastBoardSnapshot();
            return;
        }

        // Split quantity: (qty-1) PENDING + 1 DONE
        src.setQuantity(qty - 1);
        src.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(src);

        OrderDetail readyOne = new OrderDetail();
        readyOne.setOrder(src.getOrder());
        readyOne.setMenuItem(src.getMenuItem());
        readyOne.setQuantity(1);
        readyOne.setPriceAtOrder(src.getPriceAtOrder());
        readyOne.setNotes(src.getNotes());
        readyOne.setStatus("DONE");
        readyOne.setCreatedAt(LocalDateTime.now());
        readyOne.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(readyOne);

        System.out.println("✅ [COMPLETE_ONE] Split: " + (qty - 1) + " + 1 DONE");
        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void serveAllUnits(Long orderDetailId) {
        OrderDetail src = orderDetailRepository.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        String cur = norm(src.getStatus());
        System.out.println(">>> [SERVE_ALL] orderDetailId=" + orderDetailId + ", status=" + cur);

        if (!cur.equals("DONE")) {
            throw new IllegalStateException("serve-all chỉ áp dụng cho trạng thái DONE");
        }

        src.setStatus("COMPLETED");
        src.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(src);

        System.out.println("✅ [SERVE_ALL] DONE -> COMPLETED (qty=" + src.getQuantity() + ")");
        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void completeAllUnits(Long orderDetailId) {
        OrderDetail src = orderDetailRepository.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        String cur = norm(src.getStatus());
        System.out.println(">>> [COMPLETE_ALL] orderDetailId=" + orderDetailId + ", status=" + cur);

        if (cur.equals("CANCELED") || cur.equals("COMPLETED")) {
            throw new IllegalStateException("Invalid state to complete-all: " + cur);
        }

        if (cur.equals("DONE")) {
            System.out.println(">>> [COMPLETE_ALL] Already DONE, broadcasting...");
            broadcastBoardSnapshot();
            return;
        }

        src.setStatus("DONE");
        src.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(src);

        System.out.println("✅ [COMPLETE_ALL] Set to DONE");
        broadcastBoardSnapshot();
    }

}