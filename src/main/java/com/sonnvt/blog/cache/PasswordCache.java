package com.sonnvt.blog.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PasswordCache extends BaseCache {
    public PasswordCache(@Value("${app.pwd-cache.ttl}") long cacheTtl) {
        super(CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTtl, TimeUnit.MILLISECONDS));
    }
}
