package org.example.backend.config;

import org.example.backend.websocket.AuthHandshakeInterceptor;
import org.example.backend.websocket.CoinWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final CoinWebSocketHandler handler;
    private final AuthHandshakeInterceptor interceptor;

    public WebSocketConfig(CoinWebSocketHandler handler,
                           AuthHandshakeInterceptor interceptor) {
        this.handler = handler;
        this.interceptor = interceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/coins")
                .addInterceptors(interceptor)
                .setAllowedOrigins("https://localhost:9000");
    }
}