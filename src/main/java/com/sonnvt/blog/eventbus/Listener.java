package com.sonnvt.blog.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnvt.blog.database.entity.Notification;
import com.sonnvt.blog.database.repository.FollowerRepository;
import com.sonnvt.blog.database.repository.NotificationRepository;
import com.sonnvt.blog.database.repository.UserRepository;
import com.sonnvt.blog.dto.NotificationRequest;
import com.sonnvt.blog.dto.NotificationResponse;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.enums.ENotificationType;
import com.sonnvt.blog.exception.ex.BadRequestException;
import com.sonnvt.blog.exception.ex.SystemException;
import com.sonnvt.blog.service.implement.NotificationServiceImplement;
import com.sonnvt.blog.websocket.WebSocketHandlerCustom;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class Listener {
    private final ObjectMapper mapper;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final FollowerRepository followerRepository;
    private final WebSocketHandlerCustom websocket;

    @EventListener
    @Async("asyncExecutor")
    public void handleCustomEvent(Event event) {
        String topic = event.getTopic();
        switch (topic) {
            case "GLOBAL":
                handleGlobalTopic(event);
                break;
            case "NEW_POST":
                handleNewPostTopic(event);
                break;
            default:
                throw new BadRequestException("Unknown topic: " + topic);
        }
    }

    @SneakyThrows
    private void handleGlobalTopic(Event event) {
        List<Notification> notifications = new ArrayList<>();
        String content = event.getMessage();
        NotificationRequest request = mapper.convertValue(event.getMetadata()[0], NotificationRequest.class);
        UserInfoResponse sender = mapper.convertValue(event.getMetadata()[1], UserInfoResponse.class);
        NotificationServiceImplement.PostNoti post = mapper.convertValue(event.getMetadata()[2], NotificationServiceImplement.PostNoti.class);
        List<Long> ids = userRepository.findAllIds();
        for (Long id : ids) {
            if (Objects.equals(id, sender.getId())) {
                continue;
            }
            notifications.add(Notification.builder()
                    .idRecipient(id)
                    .idSender(sender.getId())
                    .metadata(mapper.writeValueAsString(post))
                    .content(content)
                    .type(request.getNotificationType().toString()).build());
        }
        notificationRepository.saveByBatch(notifications);
        try {
            for (Notification notification : notifications) {
                websocket.sendMessage(mapper.writeValueAsString(NotificationResponse.builder()
                        .idRecipient(notification.getIdRecipient())
                        .sender(NotificationResponse.Sender.builder()
                                .id(sender.getId())
                                .firstName(sender.getFirstName())
                                .lastName(sender.getLastName())
                                .avatar(sender.getAvatar())
                                .username(sender.getUsername())
                                .build())
                        .content(content)
                        .metadata(post)
                        .read(notification.getRead())
                        .seen(notification.getSeen())
                        .notificationType(ENotificationType.GLOBAL)
                        .createdAt(notification.getCreatedAt().toString())
                        .updatedAt(notification.getUpdatedAt().toString())
                        .build()), notification.getIdRecipient().toString());
            }
        } catch (Exception e) {
            throw new SystemException("Error when send notification " + e.getMessage());
        }
    }

    @SneakyThrows
    private void handleNewPostTopic(Event event) {
        NotificationRequest request = mapper.convertValue(event.getMetadata()[0], NotificationRequest.class);
        UserInfoResponse sender = mapper.convertValue(event.getMetadata()[1], UserInfoResponse.class);
        List<Long> ids = followerRepository.findAllFollowerByUser(sender.getId());
        List<Notification> notifications = new ArrayList<>();
        String content = event.getMessage();
        NotificationServiceImplement.PostNoti post = mapper.convertValue(event.getMetadata()[2], NotificationServiceImplement.PostNoti.class);
        for (Long id : ids) {
            notifications.add(Notification.builder()
                    .idRecipient(id)
                    .idSender(sender.getId())
                    .metadata(mapper.writeValueAsString(post))
                    .content(content)
                    .type(request.getNotificationType().toString()).build());
        }
        notificationRepository.saveByBatch(notifications);
        try {
            for (Notification notification : notifications) {
                websocket.sendMessage(mapper.writeValueAsString(NotificationResponse.builder()
                        .idRecipient(notification.getIdRecipient())
                        .sender(NotificationResponse.Sender.builder()
                                .id(sender.getId())
                                .firstName(sender.getFirstName())
                                .lastName(sender.getLastName())
                                .avatar(sender.getAvatar())
                                .username(sender.getUsername())
                                .build())
                        .content(content)
                        .metadata(post)
                        .read(notification.getRead())
                        .seen(notification.getSeen())
                        .notificationType(ENotificationType.NEW_POST)
                        .createdAt(notification.getCreatedAt().toString())
                        .updatedAt(notification.getUpdatedAt().toString())
                        .build()), notification.getIdRecipient().toString());
            }
        } catch (Exception e) {
            throw new SystemException("Error when send notification");
        }
    }
}
