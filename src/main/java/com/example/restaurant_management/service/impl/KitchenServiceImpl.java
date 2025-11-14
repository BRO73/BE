package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.enums.OrderItemStatus;
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
                OrderItemStatus.PENDING.name(),
                OrderItemStatus.IN_PROGRESS.name(),
                OrderItemStatus.DONE.name()
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

        String cur = od.getStatus();
        log.info("🔄 Status transition: {} -> {} for orderDetailId={}", cur, status, orderDetailId);
        System.out.println("🔄 [STATUS] " + cur + " -> " + status + " (ID: " + orderDetailId + ")");

        switch (cur) {
            case "PENDING" -> {
                if (!status.equals("DONE")
                        && !status.equals("CANCELED")
                        && !status.equals("IN_PROGRESS")) {
                    throw new IllegalStateException("Invalid transition from PENDING to " + status);
                }
            }
            case "IN_PROGRESS" -> {
                if (!status.equals("DONE") && !status.equals("CANCELED")) {
                    throw new IllegalStateException("Invalid transition from IN_PROGRESS to " + status);
                }
            }
            case "DONE" -> {
                if (!status.equals("SERVED") && !status.equals("IN_PROGRESS")) {
                    throw new IllegalStateException("Invalid transition from DONE to " + status);
                }

                if (status.equals("IN_PROGRESS")) {
                    synchronized (this) {
                        List<OrderDetail> targets = orderDetailRepository.findMergeTargetsForRollback(
                                od.getOrder().getId(),
                                od.getId(),
                                od.getMenuItem().getId(),
                                List.of("PENDING", "IN_PROGRESS"),
                                od.getPriceAtOrder(),
                                od.getNotes()
                        );

                        if (!targets.isEmpty()) {
                            OrderDetail tgt = targets.get(0);

                            if (!tgt.getOrder().getId().equals(od.getOrder().getId())) {
                                od.setStatus(status);
                                od.setUpdatedAt(LocalDateTime.now());
                                orderDetailRepository.save(od);
                                broadcastBoardSnapshot();
                                return;
                            }

                            tgt.setQuantity(tgt.getQuantity() + od.getQuantity());
                            tgt.setUpdatedAt(LocalDateTime.now());
                            orderDetailRepository.save(tgt);

                            orderDetailRepository.delete(od);
                            log.info("✅ Rollback merged: {} units back to orderDetailId={}", od.getQuantity(), tgt.getId());
                            System.out.println("✅ [ROLLBACK] Merged " + od.getQuantity() + " units to ID: " + tgt.getId());
                            broadcastBoardSnapshot();
                            return;
                        }
                    }
                }
            }
            case "SERVED", "CANCELED" -> {
                throw new IllegalStateException("Item already finished. Current status=" + cur);
            }
        }

        od.setStatus(status);
        od.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(od);

        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void completeOneUnit(Long orderDetailId) {
        OrderDetail src = orderDetailRepository.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        String cur = src.getStatus();
        System.out.println(">>> [COMPLETE_ONE] orderDetailId=" + orderDetailId + ", status=" + cur);

        if (cur.equals("CANCELED") || cur.equals("SERVED")) {
            throw new IllegalStateException("Invalid state to complete-one: " + cur);
        }

        if (cur.equals("DONE")) {
            System.out.println(">>> [COMPLETE_ONE] Already DONE, broadcasting...");
            broadcastBoardSnapshot();
            return;
        }

        int qty = src.getQuantity();
        if (qty <= 1) {
            src.setStatus("DONE");
            src.setUpdatedAt(LocalDateTime.now());
            orderDetailRepository.save(src);
            System.out.println("✅ [COMPLETE_ONE] Set to DONE (qty was 1)");
            broadcastBoardSnapshot();
            return;
        }

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

        System.out.println("✅ [COMPLETE_ONE] Split: " + (qty-1) + " + 1 DONE");
        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void serveOneUnit(Long orderDetailId) {
        OrderDetail src = orderDetailRepository.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        System.out.println(">>> [SERVE_ONE] orderDetailId=" + orderDetailId + ", status=" + src.getStatus());

        if (!src.getStatus().equals("DONE")) {
            throw new IllegalStateException("serve-one chỉ áp dụng cho trạng thái DONE");
        }

        int qty = src.getQuantity();
        if (qty <= 1) {
            orderDetailRepository.delete(src);
            System.out.println("✅ [SERVE_ONE] Deleted (qty was 1)");
            broadcastBoardSnapshot();
            return;
        }

        src.setQuantity(qty - 1);
        src.setUpdatedAt(LocalDateTime.now());
        orderDetailRepository.save(src);

        System.out.println("✅ [SERVE_ONE] Decreased qty: " + qty + " -> " + (qty-1));
        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void completeAllUnits(Long orderDetailId) {
        OrderDetail src = orderDetailRepository.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        String cur = src.getStatus();
        System.out.println(">>> [COMPLETE_ALL] orderDetailId=" + orderDetailId + ", status=" + cur);

        if (cur.equals("CANCELED") || cur.equals("SERVED")) {
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