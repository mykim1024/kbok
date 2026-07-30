package com.example.kbok.crawler.kbo;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.kbok.crawler.kbo.dto.BatterStatBasic1Dto;
import com.example.kbok.crawler.kbo.dto.BatterStatBasic2Dto;
import com.example.kbok.crawler.kbo.dto.BatterStatDto;
import com.example.kbok.crawler.kbo.mapper.BatterStatMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatterStatService {

    private final BatterStatCrawler crawler;
    private final BatterStatMapper mapper;

    public int syncBatter(String season, String teamCode) throws InterruptedException, IOException{
        List<BatterStatBasic1Dto> basic1List = crawler.crawlBasic1(teamCode, season);
        List<BatterStatBasic2Dto> basic2List = crawler.crawlBasic2(teamCode, season);

        List<BatterStatDto> merged = mergedResult(basic1List, basic2List, Integer.parseInt(season));
        return saveAll(merged, teamCode);
    }    

    public List<BatterStatDto> mergedResult(List<BatterStatBasic1Dto> basic1List, List<BatterStatBasic2Dto> basic2List, Integer season){
        Map<Long, BatterStatBasic1Dto> basic1Map = basic1List.stream()
            .collect(Collectors.toMap(BatterStatBasic1Dto::getPlayerId, Function.identity()));  
        
        return basic2List.stream()
            .map(basic2 -> {
                BatterStatBasic1Dto basic1 = basic1Map.get(basic2.getPlayerId());
                if (basic1 == null) {
                    log.warn("Basic1에 없는 kboPlayerId={} - Basic2 데이터 스킵", basic2.getPlayerId());
                    return null;
                }
                return BatterStatDto.merge(basic1, basic2, season);
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Transactional
    public int saveAll(List<BatterStatDto> list, String teamCode){
        int successCount = 0;

        for(BatterStatDto dto : list){
             try {
                mapper.upsert(dto);
                successCount++;
             } catch (Exception e) {
                log.warn("[BatterStat] 저장 실패 - name={}, error={}", dto.getName(), e.getMessage());
             }
        }
        
        
        log.info("[BatterStat] {} 완료 - {}/{}", teamCode, successCount, list.size());
        return successCount;
    }
}
