package com.sonnvt.blog.service.implement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sonnvt.blog.database.entity.Notification;
import com.sonnvt.blog.database.repository.FollowerRepository;
import com.sonnvt.blog.database.repository.NotificationRepository;
import com.sonnvt.blog.database.repository.UserRepository;
import com.sonnvt.blog.dto.NotificationRequest;
import com.sonnvt.blog.dto.NotificationResponse;
import com.sonnvt.blog.dto.UserInfoResponse;
import com.sonnvt.blog.enums.ENotificationType;
import com.sonnvt.blog.eventbus.Publisher;
import com.sonnvt.blog.exception.ex.BadRequestException;
import com.sonnvt.blog.exception.ex.SystemException;
import com.sonnvt.blog.security.UserPrincipal;
import com.sonnvt.blog.service.NotificationService;
import com.sonnvt.blog.websocket.WebSocketHandlerCustom;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImplement implements NotificationService {
    private final WebSocketHandlerCustom websocket;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final Publisher publisher;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void send(NotificationRequest request) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        long idSender = userPrincipal.getId();
        UserInfoResponse sender = userRepository.findById(idSender).orElse(null);
        if (sender == null) {
            throw new SystemException("Sender not found");
        }
        switch (request.getNotificationType()) {
            case COMMENT:
                sendCommentNotification(request, sender);
                break;
            case REPLY_COMMENT:
                sendReplyCommentNotification(request, sender);
                break;
            case GLOBAL:
                sendGlobalNotification(request, sender);
                break;
            case FOLLOW:
                sendFollowNotification(request, sender);
                break;
            case NEW_POST:
                sendNewPostNotification(request, sender);
                break;
            case MENTION:
                sendMentionNotification(request, sender);
                break;
            case REPORT:
                sendReportNotification(request, sender);
                break;
            default:
                throw new BadRequestException("Invalid notification type");
        }
    }

    @Override
    public List<NotificationResponse> get(Long idPivot) {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return notificationRepository.get(userPrincipal.getId(), idPivot);
    }

    @SneakyThrows
    private void sendCommentNotification(NotificationRequest request, UserInfoResponse sender) {
        PostNoti post = (PostNoti) request.getMetadata();
        String content = "User " + sender.getFirstName() + " " + sender.getLastName() + " commented on your post : " + post.getTitle();
        Notification noti = notificationRepository.save(request.getIdRecipient(), sender.getId(), content,
                mapper.writeValueAsString(post), request.getNotificationType().toString());
        try {
            websocket.sendMessage(mapper.writeValueAsString(NotificationResponse.builder()
                    .id(noti.getId())
                    .idRecipient(noti.getIdRecipient())
                    .sender(NotificationResponse.Sender.builder()
                            .id(sender.getId())
                            .firstName(sender.getFirstName())
                            .lastName(sender.getLastName())
                            .avatar(sender.getAvatar())
                            .username(sender.getUsername())
                            .build())
                    .content(content)
                    .metadata(post)
                    .read(noti.getRead())
                    .seen(noti.getSeen())
                    .notificationType(ENotificationType.COMMENT)
                    .createdAt(noti.getCreatedAt().toString())
                    .updatedAt(noti.getUpdatedAt().toString())
                    .build()), noti.getIdRecipient().toString());
        } catch (Exception e) {
            throw new SystemException("Error when send notification");
        }
    }

    @SneakyThrows
    private void sendFollowNotification(NotificationRequest request, UserInfoResponse sender) {
        String content = "User " + sender.getFirstName() + " " + sender.getLastName() + " start following you!";
        Notification noti = notificationRepository.save(request.getIdRecipient(), sender.getId(), content,
                mapper.writeValueAsString(sender), request.getNotificationType().toString());
        try {
            websocket.sendMessage(mapper.writeValueAsString(NotificationResponse.builder()
                    .id(noti.getId())
                    .idRecipient(noti.getIdRecipient())
                    .sender(NotificationResponse.Sender.builder()
                            .id(sender.getId())
                            .firstName(sender.getFirstName())
                            .lastName(sender.getLastName())
                            .avatar(sender.getAvatar())
                            .username(sender.getUsername())
                            .build())
                    .content(content)
                    .metadata(sender)
                    .read(noti.getRead())
                    .seen(noti.getSeen())
                    .notificationType(ENotificationType.FOLLOW)
                    .createdAt(noti.getCreatedAt().toString())
                    .updatedAt(noti.getUpdatedAt().toString())
                    .build()), noti.getIdRecipient().toString());
        } catch (Exception e) {
            throw new SystemException("Error when send notification");
        }
    }

    @SneakyThrows
    private void sendMentionNotification(NotificationRequest request, UserInfoResponse sender) {
        CommentNoti comment = (CommentNoti) request.getMetadata();
        String content = "User " + sender.getFirstName() + " " + sender.getLastName() + " mention you in a comment : " + comment.getContent();
        Notification noti = notificationRepository.save(request.getIdRecipient(), sender.getId(), content,
                mapper.writeValueAsString(sender), request.getNotificationType().toString());
        try {
            websocket.sendMessage(mapper.writeValueAsString(NotificationResponse.builder()
                    .id(noti.getId())
                    .idRecipient(noti.getIdRecipient())
                    .sender(NotificationResponse.Sender.builder()
                            .id(sender.getId())
                            .firstName(sender.getFirstName())
                            .lastName(sender.getLastName())
                            .avatar(sender.getAvatar())
                            .username(sender.getUsername())
                            .build())
                    .content(content)
                    .metadata(comment)
                    .read(noti.getRead())
                    .seen(noti.getSeen())
                    .notificationType(ENotificationType.MENTION)
                    .createdAt(noti.getCreatedAt().toString())
                    .updatedAt(noti.getUpdatedAt().toString())
                    .build()), noti.getIdRecipient().toString());
        } catch (Exception e) {
            throw new SystemException("Error when send notification");
        }
    }

    @SneakyThrows
    private void sendReplyCommentNotification(NotificationRequest request, UserInfoResponse sender) {
        CommentNoti comment = (CommentNoti) request.getMetadata();
        String content = "User " + sender.getFirstName() + " " + sender.getLastName() + " replied your comment : " + comment.getContent();
        Notification noti = notificationRepository.save(request.getIdRecipient(), sender.getId(), content,
                mapper.writeValueAsString(comment), request.getNotificationType().toString());
        try {
            websocket.sendMessage(mapper.writeValueAsString(NotificationResponse.builder()
                    .id(noti.getId())
                    .idRecipient(noti.getIdRecipient())
                    .sender(NotificationResponse.Sender.builder()
                            .id(sender.getId())
                            .firstName(sender.getFirstName())
                            .lastName(sender.getLastName())
                            .avatar(sender.getAvatar())
                            .username(sender.getUsername())
                            .build())
                    .content(content)
                    .metadata(comment)
                    .read(noti.getRead())
                    .seen(noti.getSeen())
                    .notificationType(ENotificationType.REPLY_COMMENT)
                    .createdAt(noti.getCreatedAt().toString())
                    .updatedAt(noti.getUpdatedAt().toString())
                    .build()), noti.getIdRecipient().toString());
        } catch (Exception e) {
            throw new SystemException("Error when send notification");
        }
    }

    @SneakyThrows
    private void sendGlobalNotification(NotificationRequest request, UserInfoResponse sender) {
        PostNoti post = (PostNoti) request.getMetadata();
        String content = "Admin has posted a new post : " + post.getTitle();
        publisher.send("GLOBAL", content, request, sender, post);
    }

    @SneakyThrows
    private void sendNewPostNotification(NotificationRequest request, UserInfoResponse sender) {
        PostNoti post = (PostNoti) request.getMetadata();
        String content = "User " + sender.getFirstName() + " " + sender.getLastName() + " has posted a new post : " + post.getTitle();
        publisher.send("NEW_POST", content, request, sender, post);
    }

    @SneakyThrows
    private void sendReportNotification(NotificationRequest request, UserInfoResponse sender) {
        PostNoti post = (PostNoti) request.getMetadata();
        String content = "User " + sender.getFirstName() + " " + sender.getLastName() + " report this post : " + post.getTitle();
        Notification noti = notificationRepository.save(request.getIdRecipient(), sender.getId(), content,
                mapper.writeValueAsString(post), request.getNotificationType().toString());
        try {
            websocket.sendMessage(mapper.writeValueAsString(NotificationResponse.builder()
                    .id(noti.getId())
                    .idRecipient(noti.getIdRecipient())
                    .sender(NotificationResponse.Sender.builder()
                            .id(sender.getId())
                            .firstName(sender.getFirstName())
                            .lastName(sender.getLastName())
                            .avatar(sender.getAvatar())
                            .username(sender.getUsername())
                            .build())
                    .content(content)
                    .metadata(post)
                    .read(noti.getRead())
                    .seen(noti.getSeen())
                    .notificationType(ENotificationType.REPORT)
                    .createdAt(noti.getCreatedAt().toString())
                    .updatedAt(noti.getUpdatedAt().toString())
                    .build()), noti.getIdRecipient().toString());
        } catch (Exception e) {
            throw new SystemException("Error when send notification");
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostNoti {
        private long id;
        private String title;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentNoti {
        private long id;
        private String content;
    }
}
