package com.example.restaurant_management.controller;

import com.example.restaurant_management.service.KitchenService;
import com.example.restaurant_management.config.security.filter.JWTAuthenticationFilter;
import com.example.restaurant_management.config.security.config.SecurityConfig; // nếu bạn có class này
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = KitchenController.class,
        excludeFilters = {
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JWTAuthenticationFilter.class),
                @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class) // nếu không có, bỏ dòng này
        }
)
@AutoConfigureMockMvc(addFilters = false)   // không chạy security filter chain
@DisplayName("KitchenControllerTest – completeOneUnit & completeAllUnits")
class KitchenControllerTest {

    @Autowired MockMvc mvc;

    // mock KitchenService theo chuẩn Boot 3.4+ (không dùng @MockBean)
    @org.springframework.boot.test.context.TestConfiguration
    static class MockConfig {
        @org.springframework.context.annotation.Bean
        @org.springframework.context.annotation.Primary
        KitchenService kitchenService() {
            return mock(KitchenService.class);
        }
    }

    @Autowired KitchenService service;

    @Nested
    @DisplayName("PATCH /api/kitchen/order-details/{id}/complete-one")
    class CompleteOne {
        @Test
        @DisplayName("Gọi service.completeOneUnit và trả 200 OK")
        void ok() throws Exception {
            mvc.perform(patch("/api/kitchen/order-details/5/complete-one"))
                    .andExpect(status().isOk());
            verify(service).completeOneUnit(5L);
        }
    }

    @Nested
    @DisplayName("PATCH /api/kitchen/order-details/{id}/complete-all")
    class CompleteAll {
        @Test
        @DisplayName("Gọi service.completeAllUnits và trả 200 OK")
        void ok() throws Exception {
            mvc.perform(patch("/api/kitchen/order-details/6/complete-all"))
                    .andExpect(status().isOk());
            verify(service).completeAllUnits(6L);
        }
    }
}
