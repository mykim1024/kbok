package com.example.kbok.domain.player.cache;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.example.kbok.config.CacheConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PlayerDetailCacheEvictor {
    
    @CacheEvict(cacheNames = {CacheConfig.BATTER_DETAIL, CacheConfig.PITCHER_DETAIL}, allEntries = true)
    public void evictAll(){
        log.info("선수 상세 캐시 전체 삭제");
    }
}
