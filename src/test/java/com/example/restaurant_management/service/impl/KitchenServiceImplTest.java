package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.OrderDetailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentMatchers;

@ExtendWith(MockitoExtension.class)
@DisplayName("KitchenServiceImplTest – Hoàn thành từng đơn vị & toàn bộ")
class KitchenServiceImplTest {

    private static final String TOPIC = "/topic/kitchen/board";

    @Mock OrderDetailRepository repo;
    @Mock SimpMessagingTemplate simp;

    @InjectMocks KitchenServiceImpl service;

    @Captor ArgumentCaptor<OrderDetail> odCaptor;

    // ==== Builders ====
    private static Order order(long id) {
        Order o = new Order();
        o.setId(id);
        return o;
    }
    private static OrderDetail od(long id, Order order, int qty, String status) {
        OrderDetail d = new OrderDetail();
        d.setId(id);
        d.setOrder(order);
        d.setQuantity(qty);
        d.setStatus(status);
        d.setPriceAtOrder(new BigDecimal("100000"));
        d.setNotes("no chili");
        d.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        d.setUpdatedAt(LocalDateTime.now().minusMinutes(5));
        return d;
    }

    // ===== completeOneUnit =====
    @Nested
    @DisplayName("completeOneUnit")
    class CompleteOneUnit {

        @Test
        @DisplayName("UT_KITCHEN_001 – Qty > 1 → tách 1 đơn vị sang dòng DONE mới + giảm số lượng dòng gốc + broadcast")
        void UT_KITCHEN_001_whenQtyGreaterThan1_splitsOneUnitIntoNewDoneRow_andBroadcasts() {
            Order o = order(1L);
            OrderDetail src = od(101L, o, 3, "IN_PROGRESS");
            when(repo.findByIdForUpdate(101L)).thenReturn(Optional.of(src));

            service.completeOneUnit(101L);

            verify(repo, atLeastOnce()).save(src);
            assertEquals(2, src.getQuantity());

            verify(repo, atLeast(2)).save(odCaptor.capture());
            List<OrderDetail> saved = odCaptor.getAllValues();
            OrderDetail newRow = saved.stream()
                    .filter(x -> x != src)
                    .filter(x -> "DONE".equals(x.getStatus()))
                    .findFirst().orElse(null);
            assertNotNull(newRow);
            assertEquals(1, newRow.getQuantity());

            verify(simp).convertAndSend(eq(TOPIC), ArgumentMatchers.<Object>any());
        }

        @Test
        @DisplayName("UT_KITCHEN_002 – Qty = 1 → cập nhật tại chỗ sang DONE + broadcast")
        void UT_KITCHEN_002_whenQtyIs1_setsStatusDone_inPlace_andBroadcasts() {
            Order o = order(2L);
            OrderDetail src = od(201L, o, 1, "IN_PROGRESS");
            when(repo.findByIdForUpdate(201L)).thenReturn(Optional.of(src));

            service.completeOneUnit(201L);

            assertEquals("DONE", src.getStatus());
            verify(repo).save(src);
            verify(simp).convertAndSend(eq(TOPIC), ArgumentMatchers.<Object>any());
        }

        @Test
        @DisplayName("UT_KITCHEN_003 – Đã DONE sẵn → không thay đổi dữ liệu nhưng vẫn broadcast")
        void UT_KITCHEN_003_whenAlreadyDone_doesNotChangeButStillBroadcasts() {
            Order o = order(3L);
            OrderDetail src = od(301L, o, 2, "DONE");
            when(repo.findByIdForUpdate(301L)).thenReturn(Optional.of(src));

            service.completeOneUnit(301L);

            verify(repo, never()).delete(any());
            verify(simp).convertAndSend(eq(TOPIC), ArgumentMatchers.<Object>any());
        }

        @Test
        @DisplayName("UT_KITCHEN_004 – Trạng thái CANCELED hoặc SERVED → ném IllegalStateException")
        void UT_KITCHEN_004_whenCanceledOrServed_throws() {
            Order o = order(4L);

            OrderDetail canceled = od(401L, o, 1, "CANCELED");
            when(repo.findByIdForUpdate(401L)).thenReturn(Optional.of(canceled));
            assertThrows(IllegalStateException.class, () -> service.completeOneUnit(401L));

            OrderDetail served = od(402L, o, 1, "SERVED");
            when(repo.findByIdForUpdate(402L)).thenReturn(Optional.of(served));
            assertThrows(IllegalStateException.class, () -> service.completeOneUnit(402L));
        }
    }

    // ===== completeAllUnits =====
    @Nested
    @DisplayName("completeAllUnits")
    class CompleteAllUnits {

        @Test
        @DisplayName("UT_KITCHEN_005 – Từ IN_PROGRESS → set DONE + broadcast")
        void UT_KITCHEN_005_fromInProgress_setsDone_andBroadcasts() {
            Order o = order(20L);
            OrderDetail src = od(2001L, o, 3, "IN_PROGRESS");
            when(repo.findByIdForUpdate(2001L)).thenReturn(Optional.of(src));

            service.completeAllUnits(2001L);

            assertEquals("DONE", src.getStatus());
            verify(repo).save(src);
            verify(simp).convertAndSend(eq(TOPIC), ArgumentMatchers.<Object>any());
        }

        @Test
        @DisplayName("UT_KITCHEN_006 – Đã DONE sẵn → chỉ broadcast, không save")
        void UT_KITCHEN_006_fromDone_onlyBroadcasts() {
            Order o = order(21L);
            OrderDetail src = od(2101L, o, 2, "DONE");
            when(repo.findByIdForUpdate(2101L)).thenReturn(Optional.of(src));

            service.completeAllUnits(2101L);

            verify(repo, never()).save(src);
            verify(simp).convertAndSend(eq(TOPIC), ArgumentMatchers.<Object>any());
        }

        @Test
        @DisplayName("UT_KITCHEN_007 – Trạng thái CANCELED/SERVED → ném IllegalStateException")
        void UT_KITCHEN_007_fromCanceledOrServed_throws() {
            Order o = order(22L);

            OrderDetail canceled = od(2201L, o, 1, "CANCELED");
            when(repo.findByIdForUpdate(2201L)).thenReturn(Optional.of(canceled));
            assertThrows(IllegalStateException.class, () -> service.completeAllUnits(2201L));

            OrderDetail served = od(2202L, o, 1, "SERVED");
            when(repo.findByIdForUpdate(2202L)).thenReturn(Optional.of(served));
            assertThrows(IllegalStateException.class, () -> service.completeAllUnits(2202L));
        }
    }
}
