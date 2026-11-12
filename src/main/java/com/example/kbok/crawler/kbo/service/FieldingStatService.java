package com.example.kbok.crawler.kbo.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.kbok.crawler.kbo.BatterStatCrawler;
import com.example.kbok.crawler.kbo.dto.FieldingStatDto;
import com.example.kbok.crawler.kbo.mapper.FieldingStatMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FieldingStatService {

    private final BatterStatCrawler crawler;
    private final FieldingStatMapper mapper;

    public int syncFielding(String season, String teamCode) throws IOException, InterruptedException {
        List<FieldingStatDto> list = crawler.crawlFielding(teamCode, season);
        Integer seasonValue = Integer.parseInt(season);

        int successCount = 0;
        for (FieldingStatDto dto : list) {
            try {
                mapper.upsert(dto, seasonValue);
                successCount++;
            } catch (Exception e) {
                log.warn("[FieldingStat] 저장 실패 - name={}, pos={}, error={}", dto.getName(), dto.getPosition(), e.getMessage());
            }
        }

        log.info("[FieldingStat] {} 완료 - {}/{}", teamCode, successCount, list.size());
        return successCount;
    }
}
