package com.example.kbok.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kbok.domain.player.dto.BatterDetailResponse;
import com.example.kbok.domain.player.dto.BatterListItem;
import com.example.kbok.domain.player.dto.BatterSearchCondition;
import com.example.kbok.domain.player.popular.PlayerType;
import com.example.kbok.domain.player.popular.PlayerViewCounter;
import com.example.kbok.domain.player.service.BatterQueryService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batters")
public class BatterQueryController {
    
    private final BatterQueryService batterQueryService;
    private final PlayerViewCounter playerViewCounter;

    @GetMapping
    public Page<BatterListItem> findBySeason(BatterSearchCondition cond, @PageableDefault(size = 20) Pageable pageable) {
        return batterQueryService.search(cond, pageable);
    }

    @GetMapping("/{kboPlayerId}")
    public BatterDetailResponse findDetail(
            @PathVariable String kboPlayerId,
            @RequestParam Integer season) {
        
        BatterDetailResponse response = batterQueryService.findDetail(kboPlayerId, season);
        playerViewCounter.increase(PlayerType.BATTER, kboPlayerId);
        return response;
    }

}
