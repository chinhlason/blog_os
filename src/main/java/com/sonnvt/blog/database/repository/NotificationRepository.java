package com.sonnvt.blog.database.repository;

import com.sonnvt.blog.database.entity.Notification;
import com.sonnvt.blog.dto.NotificationResponse;

import java.util.List;

public interface NotificationRepository {
    Notification save(long idRecipient, long idSender, String content, String metadata, String notificationType);
    void saveByBatch(List<Notification> notifications);
    List<NotificationResponse> get(long idRecipient, long idPivot);
}
