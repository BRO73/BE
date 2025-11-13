package com.example.restaurant_management.controller;

import com.example.restaurant_management.dto.request.SignInRequest;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false) // <- vẫn giữ để tắt Security Filter thật
@Import(AuthenticationControllerTest.MockConfig.class)
class AuthenticationControllerTest {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public AuthenticationService authenticationService() {
            return Mockito.mock(AuthenticationService.class);
        }

        @Bean
        public com.example.restaurant_management.service.JWTService jwtService() {
            return Mockito.mock(com.example.restaurant_management.service.JWTService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    // TC_LOGIN_001: Thành công
    @Test
    @DisplayName("POST /api/auth/login - 200 OK + TokenResponse")
    void login_Success_ShouldReturn200() throws Exception {
        TokenResponse fakeToken = TokenResponse.builder()
                .accessToken("access-123")
                .refreshToken("refresh-456")
                .expiresIn(3600000L)
                .build();

        Mockito.when(authenticationService.authenticate(any(SignInRequest.class)))
                .thenReturn(fakeToken);

        String body = """
                {"username":"alice","password":"secret"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("access-123"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-456"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    // TC_LOGIN_002: Sai mật khẩu
    @Test
    @DisplayName("POST /api/auth/login - Sai mật khẩu → 401 Unauthorized")
    void login_InvalidPassword_ShouldReturn401() throws Exception {
        Mockito.when(authenticationService.authenticate(any(SignInRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        String body = """
                {"username":"alice","password":"wrong"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }

    // TC_LOGIN_003: Thiếu trường / trống (Validation @Valid)
    @Test
    @DisplayName("POST /api/auth/login - Thiếu username/password → 400 Bad Request")
    void login_MissingFields_ShouldReturn400() throws Exception {
        String body = """
                {"username": "", "password": ""}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // TC_LOGIN_004: Lỗi hệ thống (RuntimeException)
    @Test
    @DisplayName("POST /api/auth/login - Lỗi hệ thống → 500 Internal Server Error")
    void login_SystemError_ShouldReturn500() throws Exception {
        Mockito.when(authenticationService.authenticate(any(SignInRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        String body = """
                {"username":"user","password":"123"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isInternalServerError());
    }

    // TC_LOGIN_005: Có roles rỗng (service trả token bình thường)
    @Test
    @DisplayName("POST /api/auth/login - Token sinh thành công dù roles rỗng")
    void login_NoRoles_ShouldReturnTokenNormally() throws Exception {
        TokenResponse fakeToken = TokenResponse.builder()
                .accessToken("token-empty-role")
                .refreshToken("refresh-empty-role")
                .expiresIn(999999L)
                .build();

        Mockito.when(authenticationService.authenticate(any(SignInRequest.class)))
                .thenReturn(fakeToken);

        String body = """
                {"username":"bob","password":"abc123"}
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("token-empty-role"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-empty-role"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
}
