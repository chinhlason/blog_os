package com.sonnvt.blog.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public abstract class BaseCache {
    private final Cache<String, Object> cache;

    public BaseCache(CacheBuilder<Object, Object> cacheBuilder) {
        cache = cacheBuilder.build();
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
