package com.example.restaurant_management.service.impl;

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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

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
        ReflectionTestUtils.setField(authenticationService, "jwtExpiration", 3_600_000L); // 1h
        System.out.println(">>> [Setup] Mocks initialized, jwtExpiration set to 3600000L (1 hour)");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println(">>> [Teardown] Mocks closed\n");
    }

    @Test
    void authenticate_ShouldReturnTokenResponse_WhenCredentialsValid() {
        System.out.println("\n=== [TEST START] authenticate_ShouldReturnTokenResponse_WhenCredentialsValid ===");

        // ARRANGE
        System.out.println("[Arrange] Preparing request and mocks...");
        SignInRequest request = new SignInRequest("user1", "password");

        List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");
        CredentialPayload credential = CredentialPayload.builder().userId(1L).build();
        Authentication authToken = new UsernamePasswordAuthenticationToken("user1", credential, authorities);

        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenAnswer(invocation -> {
                    System.out.println("[Mock] authenticationProvider.authenticate() called");
                    return authToken;
                });

        when(jwtService.generateToken(anyMap(), eq(authToken)))
                .thenAnswer(invocation -> {
                    Map<String, Object> claims = invocation.getArgument(0);
                    System.out.println("[Mock] jwtService.generateToken() called with claims: " + claims);
                    return "access-token";
                });

        when(jwtService.generateRefreshToken(anyMap(), eq(authToken)))
                .thenAnswer(invocation -> {
                    Map<String, Object> claims = invocation.getArgument(0);
                    System.out.println("[Mock] jwtService.generateRefreshToken() called with claims: " + claims);
                    return "refresh-token";
                });

        // ACT
        System.out.println("[Act] Calling authenticationService.authenticate()...");
        TokenResponse result = authenticationService.authenticate(request);
        System.out.println("[Act] AuthenticationService returned: " + result);

        // ASSERT
        System.out.println("[Assert] Validating response...");
        assertNotNull(result);
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertTrue(result.expiresIn() > System.currentTimeMillis());

        verify(authenticationProvider, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(anyMap(), eq(authToken));
        verify(jwtService, times(1)).generateRefreshToken(anyMap(), eq(authToken));
        verifyNoMoreInteractions(jwtService, authenticationProvider);

        System.out.println("=== [TEST END] authenticate_ShouldReturnTokenResponse_WhenCredentialsValid ===\n");
    }
}
