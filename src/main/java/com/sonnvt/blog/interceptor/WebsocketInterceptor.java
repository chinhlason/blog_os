package com.sonnvt.blog.interceptor;

import com.sonnvt.blog.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class WebsocketInterceptor implements HandshakeInterceptor {
    private final JwtUtils jwtUtils;
    @Override
    public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                                   @NotNull WebSocketHandler wsHandler, @NotNull Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String path = servletRequest.getServletRequest().getRequestURI();
            String[] paths = path.split("/");
            if (paths.length > 1) {
                String token = paths[paths.length - 1];
                if (jwtUtils.isValidToken(token)) {
                    long userId = jwtUtils.extractUserId(token);
                    attributes.put("userId", userId);
                }
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response,
                               @NotNull WebSocketHandler wsHandler, Exception exception) {

    }
}
