package com.example.kbok.crawler.kbo.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.kbok.crawler.kbo.BatterStatCrawler;
import com.example.kbok.crawler.kbo.dto.BatterStatBasic1Dto;
import com.example.kbok.crawler.kbo.dto.BatterStatBasic2Dto;
import com.example.kbok.crawler.kbo.dto.BatterStatDto;
import com.example.kbok.crawler.kbo.dto.RunnerStatDto;
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

        // 타석이 0이면 타격 기록이 없는 선수(투수, 대주자 전용 등)라 저장하지 않는다.
        // 포지션이 아니라 타석으로 거르는 이유: 투수도 타석에 서면 진짜 타격 기록이고,
        // 야수도 대주자로만 나오면 타격 기록이 없기 때문이다.
        List<BatterStatDto> batters = merged.stream()
            .filter(dto -> dto.getPa() != null && dto.getPa() > 0)
            .collect(Collectors.toList());
        int excluded = merged.size() - batters.size();
        if (excluded > 0) {
            log.info("[BatterStat] {} 타석 없는 선수 {}명 제외 (투수·대주자 전용 등)", teamCode, excluded);
        }

        return saveAll(batters, teamCode);
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

    /** 주루 기록 수집 → 저장된 타자 기록에 도루·도루 실패 반영. 타자 기록을 먼저 수집한 뒤 호출해야 한다. */
    public int syncRunning(String season, String teamCode) throws IOException, InterruptedException {
        List<RunnerStatDto> runners = crawler.crawlRunner(teamCode, season);
        Integer seasonValue = Integer.parseInt(season);

        int updated = 0;
        int noBatting = 0;
        for (RunnerStatDto dto : runners) {
            try {
                if (mapper.updateRunning(dto, seasonValue)) {
                    updated++;
                } else {
                    // 타석 없는 선수는 타자 기록을 저장하지 않으므로 주루 기록도 반영할 곳이 없다
                    noBatting++;
                    log.debug("[Running] 타자 기록 없음 - name={}", dto.getName());
                }
            } catch (Exception e) {
                log.warn("[Running] 반영 실패 - name={}, error={}", dto.getName(), e.getMessage());
            }
        }

        log.info("[Running] {} 완료 - {}/{} (타석 없는 선수 {}명 제외)", teamCode, updated, runners.size(), noBatting);
        return updated;
    }
}
