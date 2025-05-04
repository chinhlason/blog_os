package com.sonnvt.blog.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class Publisher {
    private final ApplicationEventPublisher eventPublisher;

    public Publisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void send(String topic, String message, Object... metadata) {
        Event event = new Event(this, topic, message, metadata);
        eventPublisher.publishEvent(event);
    }
}
