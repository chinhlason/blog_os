package com.sonnvt.blog.config;

import com.sonnvt.blog.interceptor.WebsocketInterceptor;
import com.sonnvt.blog.security.JwtUtils;
import com.sonnvt.blog.websocket.WebSocketHandlerCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebsocketConfig implements WebSocketConfigurer {
    private final JwtUtils jwtUtils;
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandlerCustom(), "/notifications/{token}")
                .setAllowedOrigins("*")
                .addInterceptors(new WebsocketInterceptor(jwtUtils));
    }
}
