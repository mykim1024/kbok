package com.example.kbok.config;

import java.time.Duration;

import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.LoggingCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import com.example.kbok.domain.player.dto.BatterDetailResponse;
import com.example.kbok.domain.player.dto.PitcherDetailResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer{
    
    public static final String BATTER_DETAIL = "batterDetail";
    public static final String PITCHER_DETAIL = "pitcherDetail";

    private static final Duration DETAIL_TTL = Duration.ofDays(1);

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper){
        ObjectMapper cacheMaper = objectMapper.copy()
            .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        
        return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory, BatchStrategies.scan(100)))
            .withCacheConfiguration(BATTER_DETAIL, detailConfig(cacheMaper, BatterDetailResponse.class))
            .withCacheConfiguration(PITCHER_DETAIL, detailConfig(cacheMaper, PitcherDetailResponse.class))
            .build();
    }

    private <T> RedisCacheConfiguration detailConfig(ObjectMapper mapper, Class<T> type){
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(DETAIL_TTL)
            .disableCachingNullValues()
            .serializeValuesWith(SerializationPair.fromSerializer(
                new Jackson2JsonRedisSerializer<>(mapper, type)));
    }

    @Override
    public CacheErrorHandler errorHandler(){
        return new LoggingCacheErrorHandler();
    }
}
