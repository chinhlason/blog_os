package com.sonnvt.blog.api;

import com.sonnvt.blog.database.repository.CommentRepository;
import com.sonnvt.blog.database.repository.NotificationRepository;
import com.sonnvt.blog.dto.NotificationResponse;
import com.sonnvt.blog.service.AuthService;
import com.sonnvt.blog.websocket.WebSocketHandlerCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/public/test")
@RequiredArgsConstructor
public class TestController {
    private final CommentRepository commentRepository;
    private final WebSocketHandlerCustom webSocketHandlerCustom;
    private final NotificationRepository notificationRepository;
    private final AuthService authService;

    @GetMapping()
    public int test(@RequestParam Long id) {
        return commentRepository.findPageById(id);
    }

    @PostMapping
    public void send(@RequestParam String message) throws Exception {
        webSocketHandlerCustom.sendMessage(message, "3");
    }

    @GetMapping("/n")
    public List<NotificationResponse> testNotification(@RequestParam Long id, @RequestParam Long idPivot) {
        return notificationRepository.get(id, idPivot);
    }

//    @PostMapping("/rf")
//    public String refreshToken(@CookieValue("refresh-token") String token) {
//        log.info("Refresh token: {}", token);
//        return authService.refreshToken(token).getToken();
//    }
}
