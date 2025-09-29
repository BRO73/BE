package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.dto.response.KitchenBoardResponse;
import com.example.restaurant_management.dto.response.KitchenTicketResponse;
import com.example.restaurant_management.entity.OrderDetail;
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
    private static final int OVERTIME_MINUTES = 30;

    @Override
    @Transactional(readOnly = true)
    public KitchenBoardResponse board(int limit) {
        Comparator<OrderDetail> byCreated = Comparator.comparing(OrderDetail::getCreatedAt);

        List<KitchenTicketResponse> pending = repo.findByStatus("Pending").stream()
                .sorted(byCreated).limit(limit)
                .map(od -> KitchenTicketResponse.from(od, OVERTIME_MINUTES)).toList();

        List<KitchenTicketResponse> inProgress = repo.findByStatus("In Progress").stream()
                .sorted(byCreated).limit(limit)
                .map(od -> KitchenTicketResponse.from(od, OVERTIME_MINUTES)).toList();

        List<KitchenTicketResponse> ready = repo.findByStatus("Ready").stream()
                .sorted(byCreated).limit(limit)
                .map(od -> KitchenTicketResponse.from(od, OVERTIME_MINUTES)).toList();

        return KitchenBoardResponse.builder()
                .serverTime(LocalDateTime.now())
                .overtimeMinutes(OVERTIME_MINUTES)
                .pending(pending)
                .inProgress(inProgress)
                .ready(ready)
                .build();
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String next) {
        var od = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("OrderDetail not found: " + id));

        String from = od.getStatus();
        if (from.equals("Ready") || from.equals("Canceled"))
            throw new IllegalStateException("Cannot change from final state: " + from);
        if (from.equals("Pending") && !(next.equals("In Progress") || next.equals("Canceled")))
            throw new IllegalStateException("Only In Progress or Canceled allowed from Pending");
        if (from.equals("In Progress") && !(next.equals("Ready") || next.equals("Canceled")))
            throw new IllegalStateException("Only Ready or Canceled allowed from In Progress");

        od.setStatus(next);
        repo.save(od);
    }
}
