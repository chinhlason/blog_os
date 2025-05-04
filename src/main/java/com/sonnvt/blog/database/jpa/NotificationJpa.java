package com.sonnvt.blog.database.jpa;

import com.sonnvt.blog.database.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpa extends JpaRepository<Notification, Long> {
}
