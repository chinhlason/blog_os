package com.sonnvt.blog.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class TokenCache extends BaseCache {
    public TokenCache(@Value("${app.cache.ttl}") long cacheTtl) {
        super(CacheBuilder.newBuilder()
                .expireAfterWrite(cacheTtl, TimeUnit.MILLISECONDS));
    }
}
