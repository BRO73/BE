package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.constant.RoleConstant;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.entity.*;
import com.example.restaurant_management.repository.*;
import com.example.restaurant_management.service.JWTService;
import com.example.restaurant_management.service.QrAuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class QrAuthServiceImpl implements QrAuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final TableRepository tableRepository;
    private final JWTService jwtService;

    @Override
    public TokenResponse qrLogin(Long tableId) {
        // 1️⃣ Kiểm tra bàn có tồn tại
        TableEntity table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table not found"));

        // 2️⃣ Tạo username riêng cho bàn
        String username = "customer_table_" + table.getId();

        // 3️⃣ Tìm user hoặc tạo mới
        User user = userRepository.findByUsername(username)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(username)
                            .hashedPassword("QR_LOGIN")
                            .build();
                    newUser = userRepository.save(newUser);

                    // Gắn Customer
                    Customer customer = new Customer();
                    customer.setUser(newUser);
                    customerRepository.save(customer);

                    // Gắn role CUSTOMER
                    Role customerRole = roleRepository.findByName("CUSTOMER")
                            .orElseThrow(() -> new RuntimeException("Role CUSTOMER not found"));
                    UserRole userRole = UserRole.builder()
                            .userId(newUser.getId())
                            .roleId(customerRole.getId())
                            .build();
                    userRoleRepository.save(userRole);

                    return newUser;
                });

        // 4️⃣ Tạo authentication giả (không password)
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority(RoleConstant.CUSTOMER))
        );

        // 5️⃣ Build claims
        Map<String, Object> claimsAccessToken = new HashMap<>();
        claimsAccessToken.put(ClaimConstant.AUTH_USER_ID, user.getId());
        claimsAccessToken.put(ClaimConstant.AUTH_USER_ROLES, List.of(RoleConstant.CUSTOMER));
        claimsAccessToken.put(ClaimConstant.TOKEN_TYPE, ClaimConstant.ACCESS_TOKEN);
        claimsAccessToken.put("tableId", tableId);

        Map<String, Object> claimsRefreshToken = new HashMap<>();
        claimsRefreshToken.put(ClaimConstant.AUTH_USER_ID, user.getId());
        claimsRefreshToken.put(ClaimConstant.TOKEN_TYPE, ClaimConstant.REFRESH_TOKEN);

        // 6️⃣ Sinh JWT
        String accessToken = jwtService.generateToken(claimsAccessToken, authentication);
        String refreshToken = jwtService.generateRefreshToken(claimsRefreshToken, authentication);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(System.currentTimeMillis() + 600000) // 10 phút
                .build();
    }
}
