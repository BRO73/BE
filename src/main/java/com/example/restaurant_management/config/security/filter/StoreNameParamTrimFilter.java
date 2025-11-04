package com.example.restaurant_management.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Chuẩn hoá query-param storeName: URL-decode, thay '+' -> ' ', trim() và nén khoảng trắng.
 * Giúp xử lý các request lỡ có %0A (xuống dòng) ở cuối.
 */
@Component
public class StoreNameParamTrimFilter extends OncePerRequestFilter {

    private static final String QP = "storeName";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String raw = request.getParameter(QP);
        if (!StringUtils.hasText(raw)) {
            filterChain.doFilter(request, response);
            return;
        }

        final String normalized = normalize(raw);

        HttpServletRequest wrapped = new HttpServletRequestWrapper(request) {
            @Override
            public String getParameter(String name) {
                if (QP.equalsIgnoreCase(name)) return normalized;
                return super.getParameter(name);
            }
        };

        filterChain.doFilter(wrapped, response);
    }

    private static String normalize(String s) {
        String v = URLDecoder.decode(s, StandardCharsets.UTF_8); // %xx -> UTF-8
        v = v.replace('+', ' ');                                 // form-encoded '+'
        v = v.trim();                                            // cắt \n, \r, space
        v = v.replaceAll("\\s+", " ");                           // gom nhiều whitespace về 1 space
        return v;
    }
}
