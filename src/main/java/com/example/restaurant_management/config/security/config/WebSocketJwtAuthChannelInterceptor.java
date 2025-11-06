// src/main/java/com/example/restaurant_management/config/security/config/WebSocketJwtAuthChannelInterceptor.java
package com.example.restaurant_management.config.security.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.util.StringUtils;

import java.security.Principal;

/**
 * Interceptor để đọc Authorization từ CONNECT và gắn Principal.
 * Tối thiểu: cho qua nếu không có token (tùy policy). Bạn có thể buộc phải có ROLE_KITCHEN/ADMIN ở đây.
 */
public class WebSocketJwtAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            String authHeader = acc.getFirstNativeHeader("Authorization");
            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                // TODO: Verify JWT (giống Security layer HTTP), rồi build Principal từ claims
                // Ví dụ đơn giản (demo):
                Principal user = () -> "ws-user"; // thay bằng username/id thực tế
                acc.setUser(user);
            } else {
                // tùy policy: có thể reject nếu yêu cầu bắt buộc token
                // throw new IllegalArgumentException("Missing Authorization");
            }
        }
        return message;
    }
}
