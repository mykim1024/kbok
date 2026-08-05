package com.example.kbok.crawler.kbo;

import org.springframework.web.bind.annotation.RestController;

import com.example.kbok.crawler.kbo.service.BatterStatService;
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
    
    
    
}
