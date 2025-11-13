package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.dto.request.SignInRequest;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.JWTService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {

    @Mock private AuthenticationProvider authenticationProvider;
    @Mock private JWTService jwtService;
    @Mock private PasswordEncoder passwordEncoder;

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private UserRoleRepository userRoleRepository;

    @InjectMocks private AuthenticationServiceImpl authenticationService;

    private AutoCloseable closeable;

    @BeforeEach
    void initMocks() {
        closeable = MockitoAnnotations.openMocks(this);
        // set @Value field
        ReflectionTestUtils.setField(authenticationService, "jwtExpiration", 3_600_000L); // 1h
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // UT_LOGIN_001: Happy path
    @Test
    void authenticate_ShouldReturnTokenResponse_WhenCredentialsValid() {
        SignInRequest request = new SignInRequest("user1", "password");

        // ✅ đổi ROLE_USER -> ROLE_ADMIN
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN");
        CredentialPayload credential = CredentialPayload.builder().userId(1L).build();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("user1", credential, authorities);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> accessClaims = ArgumentCaptor.forClass(Map.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> refreshClaims = ArgumentCaptor.forClass(Map.class);

        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(jwtService.generateToken(accessClaims.capture(), eq(authToken))).thenReturn("access-token");
        when(jwtService.generateRefreshToken(refreshClaims.capture(), eq(authToken))).thenReturn("refresh-token");

        TokenResponse result = authenticationService.authenticate(request);

        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertTrue(result.expiresIn() > System.currentTimeMillis());

        Map<String, Object> access = accessClaims.getValue();
        assertEquals(ClaimConstant.ACCESS_TOKEN, access.get(ClaimConstant.TOKEN_TYPE));
        assertEquals(List.of("ROLE_ADMIN"), access.get(ClaimConstant.AUTH_USER_ROLES)); // ✅ admin
        assertEquals(1L, access.get(ClaimConstant.AUTH_USER_ID));

        Map<String, Object> refresh = refreshClaims.getValue();
        assertEquals(ClaimConstant.REFRESH_TOKEN, refresh.get(ClaimConstant.TOKEN_TYPE));
        assertEquals(List.of("ROLE_ADMIN"), refresh.get(ClaimConstant.AUTH_USER_ROLES)); // ✅ admin
        assertEquals(1L, refresh.get(ClaimConstant.AUTH_USER_ID));

        verify(authenticationProvider).authenticate(any());
        verify(jwtService).generateToken(anyMap(), eq(authToken));
        verify(jwtService).generateRefreshToken(anyMap(), eq(authToken));
        verifyNoMoreInteractions(jwtService, authenticationProvider);
    }


    // UT_LOGIN_002: Sai mật khẩu / không đúng trong DB
    @Test
    void authenticate_ShouldThrowBadCredentials_WhenInvalidPassword() {
        SignInRequest request = new SignInRequest("user1", "wrong");
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
        verify(authenticationProvider).authenticate(any());
        verifyNoInteractions(jwtService); // Không sinh token khi sai
    }

    // UT_LOGIN_003: Trường trống (ở service không validate) → vẫn gọi provider (edge-case để ghi nhận)
    @Test
    void authenticate_WithBlankFields_StillDelegatesToProvider() {
        SignInRequest request = new SignInRequest("", "");

        List<GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN"); // ✅ đổi sang ADMIN
        CredentialPayload credential = CredentialPayload.builder().userId(2L).build();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("", credential, authorities);

        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);
        when(jwtService.generateToken(anyMap(), eq(authToken))).thenReturn("a");
        when(jwtService.generateRefreshToken(anyMap(), eq(authToken))).thenReturn("r");

        TokenResponse res = authenticationService.authenticate(request);

        assertEquals("a", res.accessToken());
        assertEquals("r", res.refreshToken());
        verify(authenticationProvider).authenticate(any());
    }


    // UT_LOGIN_004: Non-admin users cannot access dashboard
    @Test
    void authenticate_NonAdminRole_ShouldThrowAccessDenied() {
        SignInRequest request = new SignInRequest("user", "pwd");

        // Giả lập user có role USER chứ không phải ADMIN
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");
        CredentialPayload credential = CredentialPayload.builder().userId(10L).build();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken("user", credential, authorities);

        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authToken);

        AccessDeniedException ex = assertThrows(AccessDeniedException.class,
                () -> authenticationService.authenticate(request));

        assertEquals("Only ADMIN can access dashboard", ex.getMessage());
        verify(authenticationProvider).authenticate(any());
        verifyNoInteractions(jwtService);
    }


    // UT_LOGIN_005: Lỗi hệ thống (provider lỗi bất kỳ)
    @Test
    void authenticate_SystemError_ShouldPropagate() {
        SignInRequest request = new SignInRequest("user", "123");
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("System error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));
        assertEquals("System error", ex.getMessage());
        verify(authenticationProvider).authenticate(any());
        verifyNoInteractions(jwtService);
    }
}
