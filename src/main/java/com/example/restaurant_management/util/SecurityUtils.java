package com.example.restaurant_management.util;

import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.dto.*;
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
        final String email = claims.get(ClaimConstant.AUTH_USER_EMAIL, String.class);
        final String fullname = claims.get(ClaimConstant.AUTH_USER_FULLNAME, String.class);
        final Object roleObject = claims.get(ClaimConstant.AUTH_USER_ROLES, Object.class);

        List<SimpleGrantedAuthority> roles = new ArrayList<>();
        if (roleObject instanceof List<?>) {
            roles = ((List<?>) roleObject).stream()
                    .map(item -> new SimpleGrantedAuthority(String.valueOf(item)))
                    .toList();
        }

        final CredentialPayload credentialPayload = CredentialPayload.builder()
                .userId(userId)
                .fullName(fullname)
                .email(email)
                .build();


        final Authentication auth = new UsernamePasswordAuthenticationToken(username,
                credentialPayload, roles);

        SecurityContextHolder.getContext().setAuthentication(auth);

    }
}
