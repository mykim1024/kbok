package com.example.kbok.crawler.kbo;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.kbok.crawler.kbo.dto.PlayerProfileDto;
import com.example.kbok.crawler.kbo.mapper.PlayerProfileMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlayerProfileSyncService {

    private final KboPlayerProfileCrawler crawler;
    private final PlayerProfileMapper mapper;

    public int syncTeam(String teamCode) throws IOException, InterruptedException{
        return saveAll(crawler.crawlByTeam(teamCode), teamCode);
    }

    @Transactional 
    public int saveAll(List<PlayerProfileDto> profiles, String teamCode) {
        int successCount = 0;
        for(PlayerProfileDto dto : profiles){
            try {
                mapper.upsert(dto);
                successCount++;
            } catch (Exception e) {
                log.warn("[PlayerProfile] 저장 실패 - name={}, error={}", dto.getName(), e.getMessage());
            }
        }

        log.info("[PlayerProfile] {} 완료 - {}/{}", teamCode, successCount, profiles.size());
        return successCount;
    }


}
