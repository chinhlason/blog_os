package com.sonnvt.blog.service;

import com.sonnvt.blog.dto.NotificationRequest;
import com.sonnvt.blog.dto.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void send(NotificationRequest request);
    List<NotificationResponse> get(Long idPivot);
}
