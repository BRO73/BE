package com.example.restaurant_management.config.security.filter;

import com.example.restaurant_management.constant.AuthConstant;
import com.example.restaurant_management.constant.ClaimConstant;
import com.example.restaurant_management.service.JWTService;
import com.example.restaurant_management.util.SecurityUtils;
import io.jsonwebtoken.Claims;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String path = request.getServletPath();
//        if (path.startsWith("/api/auth/") || path.startsWith("/ping")) {
//            log.info("Request path = {}", request.getServletPath());
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
//        if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(AuthConstant.BEARER)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        final String jwtToken = authorizationHeader.substring(AuthConstant.BEARER.length());
//        final Claims claims = jwtService.extractClaim(jwtToken, c -> c);
//        final String username = claims.getSubject();
//
//        final String tokenType = claims.get(ClaimConstant.TOKEN_TYPE, String.class);
//        if (!ClaimConstant.ACCESS_TOKEN.equals(tokenType)) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        SecurityUtils.authenticateFromClaimsJWT(claims, username);
//        filterChain.doFilter(request, response);
//
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();

        // ✅ Bỏ qua các endpoint public như login, ping
        if (path.startsWith("/api/auth/") || path.startsWith("/ping")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // ✅ Nếu không có token thì cứ cho qua
        if (StringUtils.isBlank(authorizationHeader) || !authorizationHeader.startsWith(AuthConstant.BEARER)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwtToken = authorizationHeader.substring(AuthConstant.BEARER.length());
            final Claims claims = jwtService.extractClaim(jwtToken, c -> c);
            final String username = claims.getSubject();
            final String tokenType = claims.get(ClaimConstant.TOKEN_TYPE, String.class);

            if (ClaimConstant.ACCESS_TOKEN.equals(tokenType)) {
                SecurityUtils.authenticateFromClaimsJWT(claims, username);
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // ✅ Token hết hạn => không cần 401, chỉ bỏ qua
            log.warn("Token expired: {}", e.getMessage());
        } catch (Exception e) {
            // ✅ Token không hợp lệ, cũng bỏ qua để không ảnh hưởng request public
            log.warn("JWT parse error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

}
