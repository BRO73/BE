package com.example.restaurant_management.service.impl;

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
        var menu = menuItemRepo.findById(menuItemId)
                .orElseThrow(() -> new IllegalArgumentException("MenuItem not found: " + menuItemId));
        menu.setStatus(available ? "Available" : "Unavailable");
        menu.setUpdatedAt(LocalDateTime.now());
        menuItemRepo.save(menu);
    }
}
