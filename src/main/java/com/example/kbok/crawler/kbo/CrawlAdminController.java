package com.example.kbok.crawler.kbo;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
public class CrawlAdminController {

    private final PlayerProfileSyncService syncService;
    private final BatterStatService batterService;

    @PostMapping("/admin/crawl/player-profile")
    public String syncPlayerProfile(@RequestParam String teamCode) throws Exception {
        int count = syncService.syncTeam(teamCode);
        return "저장 완료: " + count + "명";
    }

    @PostMapping("/admin/crawl/batter-stat")
    public String postMethodName(@RequestParam String teamCode, @RequestParam String season) throws Exception {
        int count = batterService.syncBatter(season, teamCode);
        return "저장 완료: " + count + "명";
    }
    
    
}
