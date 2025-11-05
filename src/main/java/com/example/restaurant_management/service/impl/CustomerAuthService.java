package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.config.security.user.UserDetailsImpl;
import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.dto.request.RegisterCustomerRequest;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.entity.Customer;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.CustomerRepository;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.JWTService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomerAuthService {
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    private final CustomerRepository customerRepository;
    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public TokenResponse verifyFirebaseIdToken(String idToken) {
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken, true);
            String phoneNumber = (String) decoded.getClaims().get("phone_number");
            if (phoneNumber == null || phoneNumber.isBlank()) {
                throw new RuntimeException("Firebase token không có phoneNumber.");
            }
            String normalizedPhone = normalizeToLocalVN(phoneNumber);

            Customer customer = customerRepository.findByPhoneNumber(normalizedPhone)
                    .orElseGet(() -> customerRepository.save(Customer.builder().phoneNumber(normalizedPhone).build()));

            if (customer.getUser() != null) {
                return TokenResponse.builder()
                        .accessToken(generateJwt(ClaimConstant.ACCESS_TOKEN, normalizedPhone))
                        .refreshToken(generateJwt(ClaimConstant.REFRESH_TOKEN, normalizedPhone))
                        .expiresIn(System.currentTimeMillis() + jwtExpiration)
                        .build();
            } else {
                // Nếu User chưa tồn tại -> người dùng mới -> cấp Registration Token
                return TokenResponse.builder()
                        .registrationToken(generateRegistrationToken(normalizedPhone))
                        .expiresIn(System.currentTimeMillis() + 900000L) // Cấp token tạm thời 15 phút
                        .build();
            }

        } catch (Exception e) {
            // Lỗi này giờ chỉ xảy ra khi Firebase token thật sự không hợp lệ
            throw new RuntimeException("Firebase token không hợp lệ hoặc đã hết hạn.", e);
        }
    }


    @Transactional
    public TokenResponse registerCustomer(RegisterCustomerRequest request) {
        // đảm bảo customer đã được tạo ở bước verifyFirebaseIdToken
        Customer customer = customerRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy customer. Yêu cầu xác thực số điện thoại trước."));

        // Nếu user đã tồn tại thì bỏ qua tạo mới
        if (customer.getUser() == null) {
            User user = User.builder()
                    .customer(customer)
                    .username(customer.getPhoneNumber())
                    .build();
            userRepository.save(user);

            customer.setFullName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setAddress(request.getAddress());
            customer.setUser(user);
            customerRepository.save(customer);

            Role role = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new RuntimeException("Role not found."));
            UserRole userRole = UserRole.builder()
                    .roleId(role.getId())
                    .userId(user.getId())
                    .build();
            userRoleRepository.save(userRole);
        } else {
            // cập nhật thông tin nếu muốn
            customer.setFullName(request.getFullName());
            customer.setEmail(request.getEmail());
            customer.setAddress(request.getAddress());
            customerRepository.save(customer);
        }

        return TokenResponse.builder()
                .accessToken(generateJwt(ClaimConstant.ACCESS_TOKEN, customer.getPhoneNumber()))
                .refreshToken(generateJwt(ClaimConstant.REFRESH_TOKEN, customer.getPhoneNumber()))
                .expiresIn(System.currentTimeMillis() + jwtExpiration)
                .build();
    }

    private String generateRegistrationToken(String phoneNumber) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("scope", "customer:register");

        Authentication authentication = new UsernamePasswordAuthenticationToken(phoneNumber, null, List.of());
        return jwtService.generateToken(claims, authentication);
    }

    private String generateJwt(String tokenType, String phoneNumber) {
        UserDetails user = userDetailsService.loadUserByUsername(phoneNumber);

        Map<String, Object> claims = new HashMap<>();
        claims.put(ClaimConstant.TOKEN_TYPE, tokenType);

        CredentialPayload credentialPayload = CredentialPayload.builder()
                .userId(((UserDetailsImpl) user).getUserId())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                credentialPayload,
                user.getAuthorities()
        );
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put(ClaimConstant.AUTH_USER_ROLES, roles);
        claims.put(ClaimConstant.AUTH_USER_ID, ((UserDetailsImpl) user).getUserId());
        return jwtService.generateToken(claims, authentication);
    }

    // "+84xxxxxxxxx" -> "0xxxxxxxxx" (tùy tiêu chuẩn hệ thống của bạn)
    private String normalizeToLocalVN(String e164) {
        String p = e164.trim();
        if (p.startsWith("+84")) return "0" + p.substring(3);
        return p;
    }
}
