package com.example.restaurant_management.system;

import com.example.restaurant_management.controller.KitchenController;
import com.example.restaurant_management.entity.Order;
import com.example.restaurant_management.entity.OrderDetail;
import com.example.restaurant_management.repository.OrderDetailRepository;
import com.example.restaurant_management.service.KitchenService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * System Test cho 2 API:
 *  - PATCH /api/kitchen/order-details/{id}/complete-one
 *  - PATCH /api/kitchen/order-details/{id}/complete-all
 *
 * Chạy end-to-end với H2 in-memory, bỏ security filter.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false) // tắt Security filter chain khi test controller
@ActiveProfiles("test")
@TestPropertySource(properties = {
        // override MySQL thật trong application.properties để dùng H2 in-memory:contentReference[oaicite:1]{index=1}
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.main.allow-bean-definition-overriding=true"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("KitchenSystemTest – completeOneUnit & completeAllUnits (E2E)")
class KitchenSystemTest {

    private static final String TOPIC = "/topic/kitchen/board";

    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @Autowired OrderDetailRepository odRepo;
    @Autowired SimpMessagingTemplate simp; // mock @Primary ở TestConfig

    private Long idInProgressQty3;
    private Long idInProgressQty1;
    private Long idDone;
    private Long idCanceled;
    private Long idServed;

    @TestConfiguration
    static class TestConfig {
        // Tránh WebSocket thật, chỉ cần verify được convertAndSend()
        @Bean @Primary
        SimpMessagingTemplate simpMessagingTemplate() {
            return Mockito.mock(SimpMessagingTemplate.class);
        }
        // Không cần mock KitchenService — ta test end-to-end Service + Repo thật
    }

    /** Tạo Order tối thiểu, tránh ràng buộc */
    private Order newOrder() {
        Order o = new Order();
        // set các trường tối thiểu nếu entity của bạn yêu cầu
        o.setCreatedAt(LocalDateTime.now());
        o.setUpdatedAt(LocalDateTime.now());
        em.persist(o);
        return o;
    }

    /** Tạo OrderDetail với status/qty theo nhu cầu test */
    private OrderDetail newOD(Order order, int qty, String status) {
        OrderDetail d = new OrderDetail();
        d.setOrder(order);
        d.setQuantity(qty);
        d.setStatus(status);                 // dùng 'IN_PROGRESS' / 'DONE' như bạn xác nhận
        d.setPriceAtOrder(new BigDecimal("100000"));
        d.setNotes("seed");
        d.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        d.setUpdatedAt(LocalDateTime.now().minusMinutes(5));
        em.persist(d);
        return d;
    }

    @BeforeEach
    void seed() {
        // Seed 5 dòng theo các trạng thái cần test
        Order o1 = newOrder();
        Order o2 = newOrder();
        Order o3 = newOrder();

        idInProgressQty3 = newOD(o1, 3, "IN_PROGRESS").getId();
        idInProgressQty1 = newOD(o1, 1, "IN_PROGRESS").getId();
        idDone           = newOD(o2, 2, "DONE").getId();
        idCanceled       = newOD(o3, 1, "CANCELED").getId();
        idServed         = newOD(o3, 1, "SERVED").getId();

        em.flush();
        em.clear();
        Mockito.reset(simp);
    }

    // ---------------- completeOneUnit ----------------

    @Test
    @DisplayName("UT_KITCHEN_001 – completeOneUnit: Qty > 1 → tách 1 DONE + giảm dòng gốc + 200 OK + broadcast")
    void UT_KITCHEN_001_completeOne_qtyGt1_splitAndBroadcast() throws Exception {
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-one", idInProgressQty3)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var src = odRepo.findById(idInProgressQty3).orElseThrow();
        assertThat(src.getQuantity()).isEqualTo(2);
        assertThat(src.getStatus()).isEqualTo("IN_PROGRESS");

        // kiểm tra đã tạo thêm một dòng DONE quantity=1 cùng Order
        var siblings = odRepo.findAll();
        boolean hasNewDone = siblings.stream()
                .anyMatch(d -> !"IN_PROGRESS".equals(d.getStatus())
                        && "DONE".equals(d.getStatus())
                        && d.getOrder().getId().equals(src.getOrder().getId())
                        && d.getQuantity() == 1);
        assertThat(hasNewDone).isTrue();

        Mockito.verify(simp).convertAndSend(eq(TOPIC), any(Object.class));
    }

    @Test
    @DisplayName("UT_KITCHEN_002 – completeOneUnit: Qty = 1 → cập nhật tại chỗ sang DONE + 200 OK + broadcast")
    void UT_KITCHEN_002_completeOne_qtyEq1_inPlaceDone() throws Exception {
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-one", idInProgressQty1))
                .andExpect(status().isOk());

        var src = odRepo.findById(idInProgressQty1).orElseThrow();
        assertThat(src.getStatus()).isEqualTo("DONE");
        Mockito.verify(simp).convertAndSend(eq(TOPIC), any(Object.class));
    }

    @Test
    @DisplayName("UT_KITCHEN_003 – completeOneUnit: Đã DONE → không đổi dữ liệu, 200 OK + broadcast")
    void UT_KITCHEN_003_completeOne_alreadyDone_broadcastOnly() throws Exception {
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-one", idDone))
                .andExpect(status().isOk());

        var src = odRepo.findById(idDone).orElseThrow();
        assertThat(src.getStatus()).isEqualTo("DONE");
        Mockito.verify(simp).convertAndSend(eq(TOPIC), any(Object.class));
    }

    @Test
    @DisplayName("UT_KITCHEN_004 – completeOneUnit: CANCELED/SERVED → 4xx (IllegalState) ")
    void UT_KITCHEN_004_completeOne_invalidStates_throws() throws Exception {
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-one", idCanceled))
                .andExpect(status().is4xxClientError());
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-one", idServed))
                .andExpect(status().is4xxClientError());
        // không cần verify broadcast khi 4xx
        Mockito.verifyNoInteractions(simp);
    }

    // ---------------- completeAllUnits ----------------

    @Test
    @DisplayName("UT_KITCHEN_005 – completeAllUnits: IN_PROGRESS → DONE + 200 OK + broadcast")
    void UT_KITCHEN_005_completeAll_fromInProgress_toDone() throws Exception {
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-all", idInProgressQty3))
                .andExpect(status().isOk());

        var src = odRepo.findById(idInProgressQty3).orElseThrow();
        assertThat(src.getStatus()).isEqualTo("DONE");

        Mockito.verify(simp).convertAndSend(eq(TOPIC), any(Object.class));
    }

    @Test
    @DisplayName("UT_KITCHEN_006 – completeAllUnits: Đã DONE → chỉ broadcast, 200 OK")
    void UT_KITCHEN_006_completeAll_alreadyDone_broadcastOnly() throws Exception {
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-all", idDone))
                .andExpect(status().isOk());

        var src = odRepo.findById(idDone).orElseThrow();
        assertThat(src.getStatus()).isEqualTo("DONE");

        Mockito.verify(simp).convertAndSend(eq(TOPIC), any(Object.class));
    }

    @Test
    @DisplayName("UT_KITCHEN_007 – completeAllUnits: CANCELED/SERVED → 4xx (IllegalState)")
    void UT_KITCHEN_007_completeAll_invalidStates_throws() throws Exception {
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-all", idCanceled))
                .andExpect(status().is4xxClientError());
        mvc.perform(patch("/api/kitchen/order-details/{id}/complete-all", idServed))
                .andExpect(status().is4xxClientError());
        Mockito.verifyNoInteractions(simp);
    }
}
