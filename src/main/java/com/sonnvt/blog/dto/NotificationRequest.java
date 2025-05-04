package com.sonnvt.blog.dto;

import com.sonnvt.blog.enums.ENotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    private long idRecipient;
    private Object metadata;
    private ENotificationType notificationType;
}
