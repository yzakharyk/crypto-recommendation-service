package com.zakharyk.cryptorecommendationservice.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {

    @Bean
    public Cache<String, Integer> rateLimitCache(@Value("${app.ip-blacklist.period-in-seconds}") long duration) {
        return Caffeine.newBuilder()
                .expireAfterWrite(duration, TimeUnit.SECONDS)
                .build();
    }
}
