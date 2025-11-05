package com.example.restaurant_management.config.security.config;

import com.example.restaurant_management.config.security.filter.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] AUTH_WHITELIST = {
            "/api/auth/**",
            "/ping/**",
            "/api/menu-items",
            "/api/categories",
            "/api/categories/**",
            "/api/menu-items/**",
            "/ping/**",
            "/api/users/**",
            "/api/bookings/**",
            "/api/bookings",
            "/api/files/upload",
            "/storage/**",
            "/api/orders",
            "/api/payments/webhook"
            "/api/locations",
            "/api/elements",
            "/api/tables"
    };


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(corsConfigurer -> corsConfigurer.configurationSource(request -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(List.of("http://localhost:*",
                    "https://fe-admin-jet.vercel.app/",
                    "*"));
            configuration.setAllowedHeaders(
                    Arrays.asList("Accept", "Content-Type", "Authorization"));
            configuration.setAllowedMethods(
                    Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
            configuration.setAllowCredentials(true);
            return configuration;
        }));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.setAllowedOrigins(List.of("http://localhost:8081", "http://localhost:3000"));
        conf.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        conf.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        conf.setExposedHeaders(List.of("Location"));
        conf.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }
}
