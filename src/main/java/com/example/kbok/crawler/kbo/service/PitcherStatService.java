package com.example.kbok.crawler.kbo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.example.kbok.crawler.kbo.PitcherStatCrawler;
import com.example.kbok.crawler.kbo.dto.PitcherStatBasic1Dto;
import com.example.kbok.crawler.kbo.dto.PitcherStatBasic2Dto;
import com.example.kbok.crawler.kbo.dto.PitcherStatDetailDto;
import com.example.kbok.crawler.kbo.dto.PitcherStatDto;
import com.example.kbok.crawler.kbo.mapper.PitcherStatMapper;
import com.example.kbok.crawler.kbo.parse.PitcherStatBasic1Parse;
import com.example.kbok.crawler.kbo.parse.PitcherStatBasic2Parse;
import com.example.kbok.crawler.kbo.parse.PitcherStatDetailParse;
import com.example.kbok.crawler.kbo.util.KboLinkParser;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PitcherStatService {

    private final PitcherStatCrawler crawler;
    private final PitcherStatMapper mapper;

    private static final String URL1 = "https://www.koreabaseball.com/Record/Player/PitcherBasic/Basic1.aspx";
    private static final String URL2 = "https://www.koreabaseball.com/Record/Player/PitcherBasic/Basic2.aspx";
    private static final String URL3 = "https://www.koreabaseball.com/Record/Player/PitcherBasic/Detail1.aspx";

    public int syncPitcher(String season, String teamCode) throws IOException, InterruptedException{
        Map<Long, PitcherStatBasic1Dto> basic1 = crawler.crawl(URL1, season, teamCode, new PitcherStatBasic1Parse());
        Thread.sleep(1000);
        Map<Long, PitcherStatBasic2Dto> basic2 = crawler.crawl(URL2, season, teamCode, new PitcherStatBasic2Parse());
        Thread.sleep(1000);
        Map<Long, PitcherStatDetailDto> detail = crawler.crawl(URL3, season, teamCode, new PitcherStatDetailParse());

        List<PitcherStatDto> list = mergeResult(KboLinkParser.parseInteger(season), basic1, basic2, detail);
        
        return saveAll(list, teamCode);
    }

    public List<PitcherStatDto> mergeResult(Integer season, Map<Long, PitcherStatBasic1Dto> basic1, Map<Long,PitcherStatBasic2Dto> basic2, Map<Long, PitcherStatDetailDto> detail){
        Set<Long> playerIds = basic1.keySet();
        List<PitcherStatDto> resultList = new ArrayList<>();
        
        for(Long playerId : playerIds){
            PitcherStatBasic2Dto b2 = basic2.get(playerId);
            PitcherStatDetailDto d = detail.get(playerId);
            if (b2 == null || d == null) {
                log.warn("병합 누락 - playerId: {}, basic2: {}, detail: {}", playerId, b2 != null, d != null);
                continue;
            }
            PitcherStatDto dto = PitcherStatDto.merge(season, basic1.get(playerId), b2, d);  // 리턴값을 바로 받음
            resultList.add(dto);
        }

        return resultList;
    }

    @Transactional
    public int saveAll(List<PitcherStatDto> list, String teamCode){
        int successCount = 0;

        for(PitcherStatDto dto : list){
             try {
                mapper.upsert(dto);
                successCount++;
             } catch (Exception e) {
                log.warn("[PitcherStat] 저장 실패 - name={}, error={}", dto.getName(), e.getMessage());
             }
        }
        
        
        log.info("[PitcherStat] {} 완료 - {}/{}", teamCode, successCount, list.size());
        return successCount;
    }
}
