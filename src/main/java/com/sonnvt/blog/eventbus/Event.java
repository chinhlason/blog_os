package com.sonnvt.blog.eventbus;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class Event extends ApplicationEvent {
    private final String topic;
    private final String message;
    private final Object[] metadata;

    public Event(Object source, String topic, String message, Object... metadata) {
        super(source);
        this.topic = topic;
        this.message = message;
        this.metadata = metadata;
    }
}
