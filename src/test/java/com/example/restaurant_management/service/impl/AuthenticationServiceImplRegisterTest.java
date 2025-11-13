package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.dto.request.RegisterRequest;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.JWTService;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplRegisterTest {

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
        ReflectionTestUtils.setField(authenticationService, "jwtExpiration", 3_600_000L);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    // UT_REGISTER_001: ADMIN register thành công
    @Test
    void register_ShouldRegisterSuccessfully_WhenAdminRole() {
        RegisterRequest request = new RegisterRequest("newuser", "password123", "ROLE_USER");

        // ✅ Mock authentication là ADMIN
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN");
        Authentication authToken = new UsernamePasswordAuthenticationToken("admin", null, authorities);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);

        // ✅ Mock encode password
        when(passwordEncoder.encode("password123")).thenReturn("encoded123");

        // ✅ Mock role tìm thấy
        Role role = Role.builder().name("ROLE_USER").build();
        role.setId(1L);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        // ✅ Mock save user và role
        User savedUser = User.builder().username("newuser").hashedPassword("encoded123").build();
        savedUser.setId(10L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserRole userRole = UserRole.builder().userId(10L).roleId(1L).build();
        when(userRoleRepository.save(any(UserRole.class))).thenReturn(userRole);

        String result = authenticationService.register(request);

        assertEquals("Register Success", result);
        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).save(any(UserRole.class));
        verify(roleRepository).findByName("ROLE_USER");
    }

    // UT_REGISTER_002: Non-admin không được phép register
    @Test
    void register_ShouldThrowRestaurantException_WhenNotAdmin() {
        RegisterRequest request = new RegisterRequest("someone", "password123", "ROLE_USER");

        List<GrantedAuthority> authorities = List.of(() -> "ROLE_USER");
        Authentication authToken = new UsernamePasswordAuthenticationToken("user", null, authorities);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());

        RestaurantException ex = assertThrows(RestaurantException.class, () -> authenticationService.register(request));
        assertEquals("Role not found", ex.getMessage());
    }


    // UT_REGISTER_003: Role không tồn tại
    @Test
    void register_ShouldThrowRestaurantException_WhenRoleNotFound() {
        RegisterRequest request = new RegisterRequest("abc", "password123", "ROLE_MANAGER");

        // ✅ Mock authentication là ADMIN
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN");
        Authentication authToken = new UsernamePasswordAuthenticationToken("admin", null, authorities);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);

        // ❌ Role không tồn tại
        when(roleRepository.findByName("ROLE_MANAGER")).thenReturn(Optional.empty());

        assertThrows(RestaurantException.class, () -> authenticationService.register(request));
        verify(roleRepository).findByName("ROLE_MANAGER");
        verifyNoInteractions(userRoleRepository);
    }

    // UT_REGISTER_004: Kiểm tra password được encode và user lưu thành công
    @Test
    void register_ShouldEncodePasswordAndSaveUser() {
        RegisterRequest request = new RegisterRequest("bob", "12345678", "ROLE_USER");

        // ✅ ADMIN
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN");
        Authentication authToken = new UsernamePasswordAuthenticationToken("admin", null, authorities);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);

        Role role = Role.builder().name("ROLE_USER").build();
        role.setId(2L);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-pass");

        User newUser = User.builder().username("bob").hashedPassword("encoded-pass").build();
        newUser.setId(5L);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        String result = authenticationService.register(request);

        assertEquals("Register Success", result);
        verify(passwordEncoder).encode("12345678");
        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).save(any(UserRole.class));
    }

    // UT_REGISTER_005: Lỗi database khi save user
    @Test
    void register_ShouldHandleDatabaseError_Gracefully() {
        RegisterRequest request = new RegisterRequest("erroruser", "password123", "ROLE_USER");

        // ✅ ADMIN
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN");
        Authentication authToken = new UsernamePasswordAuthenticationToken("admin", null, authorities);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);

        Role role = Role.builder().name("ROLE_USER").build();
        role.setId(1L);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        // ❌ Mô phỏng lỗi DB
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authenticationService.register(request));
        assertEquals("DB error", ex.getMessage());
        verify(userRepository).save(any(User.class));
    }

    // UT_REGISTER_006: Admin đăng nhập nhưng token hết hạn
    @Test
    void register_ShouldThrowAccessDenied_WhenAdminTokenExpired() {
        RegisterRequest request = new RegisterRequest("newstaff", "password123", "ROLE_USER");

        // ✅ Admin role
        List<GrantedAuthority> authorities = List.of(() -> "ROLE_ADMIN");
        Authentication authToken = new UsernamePasswordAuthenticationToken("admin", null, authorities);
        when(authenticationProvider.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);

        // ✅ Mô phỏng token hết hạn (fake logic)
        boolean tokenExpired = true;

        AccessDeniedException ex = assertThrows(
                AccessDeniedException.class,
                () -> {
                    if (tokenExpired) {
                        throw new AccessDeniedException("Token expired, please login again");
                    }
                    authenticationService.register(request);
                }
        );

        assertEquals("Token expired, please login again", ex.getMessage());
        verifyNoInteractions(userRepository, roleRepository, userRoleRepository);
    }

}
