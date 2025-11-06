// src/main/java/com/example/restaurant_management/config/security/config/WebSocketConfig.java
package com.example.restaurant_management.config.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // Heartbeat scheduler cho SimpleBroker
    @Bean
    public TaskScheduler brokerTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("ws-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Tùy môi trường, nên khóa origin cụ thể
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*", "http://localhost", "http://*.yourdomain.com")
                .withSockJS(); // FE đang dùng SockJS
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");

        // Simple broker + heartbeat (ms)
        registry.enableSimpleBroker("/topic")
                .setTaskScheduler(brokerTaskScheduler())
                .setHeartbeatValue(new long[]{10000, 10000}); // server->client, client->server
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Nếu bạn muốn verify JWT ở CONNECT/SUBSCRIBE:
        registration.interceptors(new WebSocketJwtAuthChannelInterceptor());
    }
}
