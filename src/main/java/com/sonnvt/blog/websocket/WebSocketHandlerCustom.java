package com.sonnvt.blog.websocket;

import com.sonnvt.blog.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandlerCustom extends TextWebSocketHandler {
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(@NotNull WebSocketSession session) throws Exception {
        Map<String, Object> attributes = session.getAttributes();
        Long userId = (Long) attributes.get("userId");
        if (userId != null) {
            userSessions.put(userId.toString(), session);
        } else {
            log.error("User id is null, can not create a websocket session");
            session.close();
        }
    }

    @Override
    public void afterConnectionClosed(@NotNull WebSocketSession session, @NotNull CloseStatus status) throws Exception {
        userSessions.remove(session.getId());
    }

    public void sendMessage(String message, String userId) throws Exception {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long currUserId = userPrincipal.getId();
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen() && currUserId != Long.parseLong(userId)) {
            session.sendMessage(new TextMessage(message));
            log.info("Message sent to user {}", userId);
        } else {
            log.error("User {} is not in the session", userId);
        }
    }
}
