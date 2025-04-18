package com.xue.mianshi.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

public class CaffeineConfig {
    private static final Cache<String, String> LOCAL_CACHE =
            Caffeine.newBuilder()
                    .initialCapacity(1024)
                    .maximumSize(10000L)
                    .expireAfterWrite(5L, TimeUnit.MINUTES)
                    .build();

    // 提供静态方法操作缓存
    public static void put(String key, String value) {
        LOCAL_CACHE.put(key, value);
    }

    public static String get(String key) {
        return LOCAL_CACHE.getIfPresent(key);
    }
}
