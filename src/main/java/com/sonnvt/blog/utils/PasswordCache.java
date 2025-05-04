package com.sonnvt.blog.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PasswordCache {
    private final Cache<String, Object> cache;

    public PasswordCache(@Value("${app.pwd-cache.ttl}") long cacheTtl) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(cacheTtl, TimeUnit.MILLISECONDS)
                .build();
    }

    public Object get(String key) {
        return cache.getIfPresent(key);
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public void remove(String key) {
        cache.invalidate(key);
    }
}
