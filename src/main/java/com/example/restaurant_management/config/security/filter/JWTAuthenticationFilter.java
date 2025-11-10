package com.example.restaurant_management.config.security.filter;

import com.example.restaurant_management.config.security.custom.CustomUserDetailsService;
import com.example.restaurant_management.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    private final JWTService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        if (HttpMethod.OPTIONS.matches(request.getMethod())) return true;
        if (MATCHER.match("/api/auth/**", path)) return true;
        if (MATCHER.match("/ping/**", path)) return true;
        if (HttpMethod.GET.matches(request.getMethod()) && MATCHER.match("/api/kitchen/**", path)) return true;
        return false;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String username;
        try {
            username = jwtService.extractUserName(token); // claim "sub"
        } catch (ExpiredJwtException ex) {
            log.debug("JWT expired: {}", ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        } catch (Exception ex) {
            log.debug("JWT parse error: {}", ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        if (!StringUtils.hasText(username) || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy storeName từ claim bằng extractClaim (ưu tiên u_store_name rồi u_storeName)
        Function<Claims, String> storeNameResolver = claims -> {
            Object s1 = claims.get("u_store_name");
            Object s2 = claims.get("u_storeName");
            if (s1 instanceof String) return (String) s1;
            if (s2 instanceof String) return (String) s2;
            return null;
        };
        String storeName = null;
        try {
            storeName = jwtService.extractClaim(token, storeNameResolver);
        } catch (Exception e) {
            log.debug("Cannot extract storeName via extractClaim: {}", e.getMessage());
        }

        // (Tùy chọn) Lấy u_id để fallback khi không tìm thấy theo username
        Function<Claims, String> userIdResolver = claims -> {
            Object uid = claims.get("u_id");
            return uid == null ? null : String.valueOf(uid);
        };
        String userIdStr = null;
        try {
            userIdStr = jwtService.extractClaim(token, userIdResolver);
        } catch (Exception ignored) { }

        UserDetails userDetails = null;
        try {
            if (StringUtils.hasText(storeName)) {
                // Quan trọng: dùng đúng hàm theo store
                userDetails = userDetailsService.loadUserByUsernameAndStoreName(username, storeName);
            } else {
                // Fallback theo username nếu thiếu storeName
                userDetails = userDetailsService.loadUserByUsername(username);
            }
        } catch (UsernameNotFoundException ex) {
            log.debug("User not found by username/store: {}/{}", username, storeName);
        } catch (Exception ex) {
            log.debug("Error loading by username/store: {}", ex.getMessage());
        }

        // Thử thêm fallback theo u_id khi có
        if (userDetails == null && StringUtils.hasText(userIdStr)) {
            try {
                Long uid = null;
                if (userIdStr.matches("\\d+")) {
                    uid = Long.parseLong(userIdStr);
                }
                if (uid != null) {
                    userDetails = userDetailsService.loadUserById(uid);
                    log.debug("Loaded user by u_id fallback: {}", uid);
                }
            } catch (Exception ex) {
                log.debug("Fallback load by u_id failed: {}", ex.getMessage());
            }
        }

        if (userDetails != null) {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}