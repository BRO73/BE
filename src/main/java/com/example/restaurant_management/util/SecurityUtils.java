package com.example.restaurant_management.util;

import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.model.CredentialPayload;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

public class SecurityUtils {
    public static void authenticateFromClaimsJWT(Claims claims, String username) {
        final Long userId = claims.get(ClaimConstant.AUTH_USER_ID, Long.class);
        final Object roleObject = claims.get(ClaimConstant.AUTH_USER_ROLES, Object.class);

        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        if (roleObject instanceof List<?>) {
            roles = ((List<?>) roleObject).stream()
                    .map(item -> new SimpleGrantedAuthority(String.valueOf(item)))
                    .toList();
        }

        final CredentialPayload credentialPayload = CredentialPayload.builder()
                .userId(userId)
                .build();

        final Authentication auth = new UsernamePasswordAuthenticationToken(username,
                credentialPayload, roles);

        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    public static Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getCredentials() instanceof CredentialPayload) {
            CredentialPayload payload = (CredentialPayload) authentication.getCredentials();
            return payload.getUserId();
        }
        return null;
    }
}
