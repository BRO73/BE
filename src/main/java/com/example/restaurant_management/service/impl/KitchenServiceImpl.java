package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.dto.response.KitchenBoardResponse;
import com.example.restaurant_management.dto.response.KitchenTicketResponse;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.repository.OrderDetailRepository;
import com.example.restaurant_management.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class KitchenServiceImpl implements KitchenService {

    private static final String TOPIC_BOARD = "/topic/kitchen/board";

    private final OrderDetailRepository repo;
    private final MenuItemRepository menuItemRepo;
    private final SimpMessagingTemplate simp;

    private int defaultLimit() {
        return 200; // tùy bạn cấu hình
    }

    /** Gửi snapshot board lên topic. */
    private void broadcastBoardSnapshot() {
        KitchenBoardResponse snapshot = board(defaultLimit());
        // Bắn snapshot ra topic
        simp.convertAndSend(TOPIC_BOARD, snapshot);
    }

    @Override
    @Transactional(readOnly = true)
    public KitchenBoardResponse board(int limit) {
        List<OrderDetail> all = repo.findAll();
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

        // WS notify
        broadcastBoardSnapshot();
    }

    /**
     * Cập nhật trạng thái OrderDetail.
     * Hỗ trợ rollback DONE -> IN_PROGRESS:
     *  - Nếu tìm được dòng gốc (PENDING/IN_PROGRESS) cùng order + menu + priceAtOrder + notes,
     *    sẽ GỘP quantity về dòng gốc và XÓA dòng DONE (giữ ID cũ của dòng gốc).
     *  - Nếu không tìm được dòng phù hợp: đổi trạng thái tại chỗ (giữ ID hiện tại).
     */
    @Override
    @Transactional
    public void updateOrderDetailStatus(Long orderDetailId, OrderItemStatus status) {
        OrderDetail od = repo.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        OrderItemStatus cur = od.getStatus();

        switch (cur) {
            case PENDING -> {
                if (status != OrderItemStatus.DONE
                        && status != OrderItemStatus.CANCELED
                        && status != OrderItemStatus.IN_PROGRESS) {
                    throw new IllegalStateException("Invalid transition from PENDING to " + status);
                }
            }
            case IN_PROGRESS -> {
                if (status != OrderItemStatus.DONE
                        && status != OrderItemStatus.CANCELED) {
                    throw new IllegalStateException("Invalid transition from IN_PROGRESS to " + status);
                }
            }
            case DONE -> {
                if (status != OrderItemStatus.SERVED
                        && status != OrderItemStatus.IN_PROGRESS) {
                    throw new IllegalStateException("Invalid transition from DONE to " + status);
                }

                if (status == OrderItemStatus.IN_PROGRESS) {
                    // TÌM DÒNG GỐC TRONG CÙNG ORDER (SIẾT CHẶT)
                    List<OrderDetail> targets = repo.findMergeTargetsForRollback(
                            od.getOrder().getId(),
                            od.getId(), // loại trừ chính nó
                            od.getMenuItem().getId(),
                            List.of(OrderItemStatus.PENDING, OrderItemStatus.IN_PROGRESS),
                            od.getPriceAtOrder(),
                            od.getNotes()
                    );

                    if (!targets.isEmpty()) {
                        OrderDetail tgt = targets.get(0);

                        // AN TOÀN HƠN: kiểm tra lại orderId (phòng trường hợp mapping khác)
                        if (!tgt.getOrder().getId().equals(od.getOrder().getId())) {
                            // Không cùng order => không gộp, fallback đổi trạng thái tại chỗ
                            od.setStatus(status);
                            od.setUpdatedAt(LocalDateTime.now());
                            repo.save(od);

                            // WS notify trước khi return sớm
                            broadcastBoardSnapshot();
                            return;
                        }

                        // Gộp quantity về dòng gốc
                        tgt.setQuantity(tgt.getQuantity() + od.getQuantity());
                        tgt.setUpdatedAt(LocalDateTime.now());
                        repo.save(tgt);

                        // Xoá dòng DONE sau khi gộp
                        repo.delete(od);

                        // WS notify trước khi return sớm
                        broadcastBoardSnapshot();
                        return;
                    }
                    // Không có dòng phù hợp -> rơi xuống đổi trạng thái tại chỗ
                }
            }
            case SERVED, CANCELED -> {
                throw new IllegalStateException("Item already finished. Current status=" + cur);
            }
        }

        // Mặc định: đổi trạng thái tại chỗ
        od.setStatus(status);
        od.setUpdatedAt(LocalDateTime.now());
        repo.save(od);

        // WS notify
        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void completeOneUnit(Long orderDetailId) {
        OrderDetail src = repo.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        OrderItemStatus cur = src.getStatus();
        if (cur == OrderItemStatus.CANCELED || cur == OrderItemStatus.SERVED) {
            throw new IllegalStateException("Invalid state to complete-one: " + cur);
        }
        if (cur == OrderItemStatus.DONE) {
            // đã DONE rồi thì không đổi gì nhưng vẫn bắn snapshot để đồng bộ UI (tuỳ chọn)
            broadcastBoardSnapshot();
            return;
        }

        int qty = src.getQuantity();
        if (qty <= 1) {
            src.setStatus(OrderItemStatus.DONE);
            src.setUpdatedAt(LocalDateTime.now());
            repo.save(src);

            // WS notify rồi return
            broadcastBoardSnapshot();
            return;
        }

        // Tách 1 đơn vị DONE ra dòng mới
        src.setQuantity(qty - 1);
        src.setUpdatedAt(LocalDateTime.now());
        repo.save(src);

        OrderDetail readyOne = new OrderDetail();
        readyOne.setOrder(src.getOrder());
        readyOne.setMenuItem(src.getMenuItem());
        readyOne.setQuantity(1);
        readyOne.setPriceAtOrder(src.getPriceAtOrder());
        readyOne.setNotes(src.getNotes());
        readyOne.setStatus(OrderItemStatus.DONE);
        readyOne.setCreatedAt(LocalDateTime.now());
        readyOne.setUpdatedAt(LocalDateTime.now());
        repo.save(readyOne);

        // WS notify
        broadcastBoardSnapshot();
    }

    @Override
    @Transactional
    public void serveOneUnit(Long orderDetailId) {
        OrderDetail src = repo.findByIdForUpdate(orderDetailId)
                .orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + orderDetailId));

        if (src.getStatus() != OrderItemStatus.DONE) {
            throw new IllegalStateException("serve-one chỉ áp dụng cho trạng thái DONE");
        }
        int qty = src.getQuantity();
        if (qty <= 1) {
            repo.delete(src);

            // WS notify rồi return
            broadcastBoardSnapshot();
            return;
        }
        src.setQuantity(qty - 1);
        src.setUpdatedAt(LocalDateTime.now());
        repo.save(src);

        // WS notify
        broadcastBoardSnapshot();
    }


}
