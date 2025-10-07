package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.enums.OrderItemStatus;
import com.example.restaurant_management.dto.response.KitchenBoardResponse;
import com.example.restaurant_management.dto.response.KitchenTicketResponse;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.MenuItemRepository;
import com.example.restaurant_management.repository.OrderDetailRepository;
import com.example.restaurant_management.service.KitchenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KitchenServiceImpl implements KitchenService {

    private final OrderDetailRepository repo;
    private final MenuItemRepository menuItemRepo;

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
                .serverTime(LocalDateTime.now())
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
    }

    @Override
    @Transactional
    public void updateOrderDetailStatus(Long orderDetailId, OrderItemStatus status) {
        OrderDetail od = repo.findById(orderDetailId)
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

                if (status != OrderItemStatus.SERVED) {
                    throw new IllegalStateException("Invalid transition from DONE to " + status);
                }
            }
            case SERVED, CANCELED -> {
                throw new IllegalStateException("Item already finished. Current status=" + cur);
            }
        }

        od.setStatus(status);
        od.setUpdatedAt(LocalDateTime.now());
        repo.save(od);
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
        if (cur == OrderItemStatus.DONE) return;

        int qty = src.getQuantity();
        if (qty <= 1) {
            src.setStatus(OrderItemStatus.DONE);
            src.setUpdatedAt(LocalDateTime.now());
            repo.save(src);
            return;
        }

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
        repo.save(readyOne);
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
            return;
        }
        src.setQuantity(qty - 1);
        src.setUpdatedAt(LocalDateTime.now());
        repo.save(src);
    }
}
