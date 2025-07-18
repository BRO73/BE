package com.example.demo_innocode.config.security.filter;

import com.example.demo_innocode.constant.AuthConstant;
import com.example.demo_innocode.constant.ClaimConstant;
import com.example.demo_innocode.service.JWTService;
import com.example.demo_innocode.util.SecurityUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(AuthConstant.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwtToken = authorizationHeader.substring(AuthConstant.BEARER.length());
        final Claims claims = jwtService.extractClaim(jwtToken, c -> c);
        final String username = claims.getSubject();

        final String tokenType = claims.get(ClaimConstant.TOKEN_TYPE, String.class);
        if (!ClaimConstant.ACCESS_TOKEN.equals(tokenType)) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityUtils.authenticateFromClaimsJWT(claims, username);
        filterChain.doFilter(request, response);

    }
}
