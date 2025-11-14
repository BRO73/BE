package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.dto.request.RegisterRequest;
import com.example.restaurant_management.dto.request.SignInRequest;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.entity.Role;
import com.example.restaurant_management.entity.Staff;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.entity.UserRole;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.RoleRepository;
import com.example.restaurant_management.repository.StaffRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.repository.UserRoleRepository;
import com.example.restaurant_management.service.AuthenticationService;
import com.example.restaurant_management.service.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationProvider authenticationProvider;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final StaffRepository staffRepository;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Override
    public TokenResponse authenticate(SignInRequest request) {
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        Map<String, Object> claimsAccessToken = buildClaimsAccessToken(authentication);
        Map<String, Object> claimsRefreshToken = buildClaimsRefreshToken(authentication);

        return TokenResponse.builder()
                .accessToken(jwtService.generateToken(claimsAccessToken, authentication))
                .refreshToken(jwtService.generateRefreshToken(claimsRefreshToken, authentication))
                .expiresIn(System.currentTimeMillis() + jwtExpiration)
                .build();
    }

    @Override
    @Transactional
    public String register(RegisterRequest request) {

        // 1. Tạo user
        User user = User.builder()
                .username(request.username())
                .hashedPassword(passwordEncoder.encode(request.password()))
                .build();
        userRepository.save(user);

        // 2. Gán role cho user
        Role role = roleRepository.findByName(request.role())
                .orElseThrow(() -> new RestaurantException("Role not found"));

        UserRole userRole = UserRole.builder()
                .roleId(role.getId())
                .userId(user.getId())
                .build();
        userRoleRepository.save(userRole);

        // 3. ⭐ TỰ ĐỘNG TẠO STAFF CHO USER MỚI
        Staff staff = Staff.builder()
                .user(user)
                .fullName(user.getUsername()) // tạm dùng username làm tên
                .email(null)
                .phoneNumber(null)
                .build();

        staffRepository.save(staff);

        return "Register Success";
    }

    private Map<String, Object> buildClaimsAccessToken(Authentication authentication) {
        Map<String, Object> claims = buildClaimsFromAuthentication(authentication);
        claims.put(ClaimConstant.TOKEN_TYPE, ClaimConstant.ACCESS_TOKEN);
        return claims;
    }

    private Map<String, Object> buildClaimsRefreshToken(Authentication authentication) {
        Map<String, Object> claims = buildClaimsFromAuthentication(authentication);
        claims.put(ClaimConstant.TOKEN_TYPE, ClaimConstant.REFRESH_TOKEN);
        return claims;
    }

    private Map<String, Object> buildClaimsFromAuthentication(Authentication authentication) {
        final Map<String, Object> claims = new LinkedHashMap<>();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        CredentialPayload credentialPayload = (CredentialPayload) authentication.getCredentials();

        claims.put(ClaimConstant.AUTH_USER_ROLES, roles);
        claims.put(ClaimConstant.AUTH_USER_ID, credentialPayload.getUserId());
        return claims;
    }
}
