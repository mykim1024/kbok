package com.example.kbok.crawler.kbo;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import com.example.kbok.crawler.kbo.service.BatterStatService;
import com.example.kbok.crawler.kbo.service.FieldingStatService;
import com.example.kbok.crawler.kbo.service.KboFullSyncService;
import com.example.kbok.crawler.kbo.service.PitcherStatService;
import com.example.kbok.crawler.kbo.service.PlayerProfileSyncService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequiredArgsConstructor
public class CrawlAdminController {

    private final PlayerProfileSyncService syncService;
    private final BatterStatService batterService;
    private final PitcherStatService pitcherStatService;
    private final KboFullSyncService fullSyncService;
    private final FieldingStatService fieldingStatService;

    @PostMapping("/admin/crawl/player-profile")
    public String syncPlayerProfile(@RequestParam String teamCode) throws Exception {
        int count = syncService.syncTeam(teamCode);
        return "저장 완료: " + count + "명";
    }

    @PostMapping("/admin/crawl/batter-stat")
    public String syncBatterStat(@RequestParam String teamCode, @RequestParam String season) throws Exception {
        int count = batterService.syncBatter(season, teamCode);
        return "저장 완료: " + count + "명";
    }

    @PostMapping("/admin/crawl/pitcher-stat")
    public String syncPitcherStat(@RequestParam String teamCode, @RequestParam String season) throws Exception {
        int count = pitcherStatService.syncPitcher(season, teamCode);
        return "저장 완료: " + count + "명";
    }
    
    
    
    @PostMapping("/admin/crawl/runner-stat")
    public String syncRunnerStat(@RequestParam String teamCode, @RequestParam String season) throws Exception {
        int count = batterService.syncRunning(season, teamCode);
        return "반영 완료: " + count + "명";
    }

    @PostMapping("/admin/crawl/fielding-stat")
    public String syncFieldingStat(@RequestParam String teamCode, @RequestParam String season) throws Exception {
        int count = fieldingStatService.syncFielding(season, teamCode);
        return "저장 완료: " + count + "건";
    }

    /** 10개 구단 선수·타자·투수 기록을 한 번에 수집 (예: POST /admin/crawl/all?season=2026) */
    @PostMapping("/admin/crawl/all")
    public Map<String, String> syncAll(@RequestParam String season) {
        return fullSyncService.syncAllTeams(season);
    }
}
