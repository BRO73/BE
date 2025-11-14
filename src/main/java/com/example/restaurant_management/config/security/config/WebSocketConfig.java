package com.example.restaurant_management.config.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Đây là endpoint (URL) mà Frontend (SockJS) sẽ kết nối tới.
        registry.addEndpoint("/ws")
                // Cho phép tất cả các origin (ví dụ: localhost:5173) kết nối
                // Sau này deploy lên production thì bạn nên khóa lại:
                // .setAllowedOriginPatterns("http://your-frontend-domain.com")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

}