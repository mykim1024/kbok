package com.example.kbok.domain.player.popular;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerViewCounter {
    
    private static final String KEY_PREFIX = "views:";
    private static final Duration KEY_TTL = Duration.ofDays(2);
    private static final ZoneId KOREA = ZoneId.of("Asia/Seoul");

    private final StringRedisTemplate redisTemplate;

    public void increase(PlayerType type, String kboPlayerId){
        String key = KEY_PREFIX + LocalDate.now(KOREA);
        String member = type.name() + ":" + kboPlayerId;
        try {
            redisTemplate.opsForZSet().incrementScore(key, member, 1);
            redisTemplate.expire(key, KEY_TTL);
        } catch (Exception e) {
            log.warn("조회수 증가 실패: member={}", member, e);
        }
    }

    public List<ViewCount> findTodayTop(int size){
        Set<ZSetOperations.TypedTuple<String>> tuples;
    
        try {
            tuples = redisTemplate.opsForZSet().reverseRangeWithScores(todayKey(), 0, size - 1);
        } catch (DataAccessException e) {
            log.error("인기 선수 조회 실패", e);
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "인기 선수를 불러올 수 없습니다", e);
        }
        
        if(tuples == null){
            return List.of();
        }

        List<ViewCount> result = new ArrayList<>();
        for(ZSetOperations.TypedTuple<String> t : tuples){
            ViewCount.parse(t.getValue(), t.getScore())
                .ifPresentOrElse(
                    result::add, 
                    () -> log.warn("잘못된 조회수 데이터 건너뜀: member={}", t.getValue()));
        }

        return result;
    }

    private String todayKey(){
        return KEY_PREFIX + LocalDate.now(KOREA);
    }
}
