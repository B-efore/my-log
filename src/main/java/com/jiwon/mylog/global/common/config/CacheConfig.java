package com.jiwon.mylog.global.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;


@Configuration
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofMinutes(30)));

        cacheManager.registerCustomCache("dailyFortune",
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofDays(1))
                        .build());

        cacheManager.registerCustomCache("stat::ranker",
                Caffeine.newBuilder()
                        .maximumSize(5)
                        .expireAfterWrite(Duration.ofDays(1))
                        .build());

        return cacheManager;
    }
}
