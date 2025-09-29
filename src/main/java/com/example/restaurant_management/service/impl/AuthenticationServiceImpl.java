package com.example.restaurant_management.service.impl;

import com.example.restaurant_management.common.constant.ErrorEnum;
import com.example.restaurant_management.common.exception.RestaurantException;
import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.dto.request.RegisterRequest;
import com.example.restaurant_management.dto.request.SignInRequest;
import com.example.restaurant_management.dto.response.TokenResponse;
import com.example.restaurant_management.entity.Store;
import com.example.restaurant_management.entity.User;
import com.example.restaurant_management.model.CredentialPayload;
import com.example.restaurant_management.repository.StoreRepository;
import com.example.restaurant_management.repository.UserRepository;
import com.example.restaurant_management.service.AuthenticationService;
import com.example.restaurant_management.service.JWTService;
import com.example.restaurant_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
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
    private final UserService userService;

    @Value("${jwt.expiration}")
    private long jwtExpiration;


    @Override
    public TokenResponse authenticate(SignInRequest request) {
        // Gộp storeName và username lại thành "storeName:username"
        String combinedUsername = request.storeName() + ":" + request.username();

        Authentication authentication;
        authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(combinedUsername, request.password())
        );


        // build claims
        Map<String, Object> claimsAccessToken = buildClaimsAccessToken(authentication);
        Map<String, Object> claimsRefreshToken = buildClaimsRefreshToken(authentication);

        return TokenResponse.builder()
                .accessToken(jwtService.generateToken(claimsAccessToken, authentication))
                .refreshToken(jwtService.generateRefreshToken(claimsRefreshToken, authentication))
                .expiresIn(System.currentTimeMillis() + jwtExpiration)
                .build();
    }


    @Override
    public String register(RegisterRequest request) {
        User user = User.builder()
                .username(request.username())
                .hashedPassword(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .email(request.email())
                .phoneNumber(request.phoneNumber())
                .store(request.storeName())
                .build();
        userRepository.save(user);
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
        claims.put(ClaimConstant.AUTH_USER_EMAIL, credentialPayload.getEmail());
        claims.put(ClaimConstant.AUTH_USER_FULLNAME, credentialPayload.getFullName());

        return claims;
    }
}
