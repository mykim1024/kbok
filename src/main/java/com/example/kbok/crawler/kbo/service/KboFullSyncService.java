package com.example.kbok.crawler.kbo.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 10개 구단 전체를 한 번에 수집한다.
 * 기록 저장 시 선수가 먼저 있어야 하므로 팀마다 "선수 프로필 → 타자 → 주루 → 투수 → 수비" 순서로 돈다.
 * 주루는 저장된 타자 기록을 갱신하므로 반드시 타자 다음에 돈다.
 * 한 팀이 실패해도 나머지 팀은 계속 수집한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KboFullSyncService {

    /** KBO 기록실 팀 코드 */
    public static final List<String> TEAM_CODES =
        List.of("HT", "SS", "LG", "OB", "SK", "KT", "NC", "LT", "HH", "WO");

    private final PlayerProfileSyncService playerProfileSyncService;
    private final BatterStatService batterStatService;
    private final PitcherStatService pitcherStatService;
    private final FieldingStatService fieldingStatService;

    public Map<String, String> syncAllTeams(String season) {
        Map<String, String> result = new LinkedHashMap<>();

        for (String teamCode : TEAM_CODES) {
            try {
                int players  = playerProfileSyncService.syncTeam(teamCode);
                Thread.sleep(1000);
                int batters  = batterStatService.syncBatter(season, teamCode);
                Thread.sleep(1000);
                int runners  = batterStatService.syncRunning(season, teamCode);
                Thread.sleep(1000);
                int pitchers = pitcherStatService.syncPitcher(season, teamCode);
                Thread.sleep(1000);
                int fielders = fieldingStatService.syncFielding(season, teamCode);
                Thread.sleep(1000);

                result.put(teamCode, String.format("선수 %d / 타자 %d / 주루 %d / 투수 %d / 수비 %d", players, batters, runners, pitchers, fielders));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                result.put(teamCode, "중단됨");
                break;
            } catch (Exception e) {
                log.error("[FullSync] {} 수집 실패", teamCode, e);
                result.put(teamCode, "실패: " + e.getMessage());
            }
        }
        return result;
    }
}
