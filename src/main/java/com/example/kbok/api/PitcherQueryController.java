package com.example.kbok.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kbok.domain.player.dto.PitcherDetailResponse;
import com.example.kbok.domain.player.dto.PitcherListItem;
import com.example.kbok.domain.player.dto.PitcherSearchCondition;
import com.example.kbok.domain.player.service.PitcherQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pitcher")
public class PitcherQueryController {
    
    private final PitcherQueryService pitcherQueryService;

    @GetMapping
    public Page<PitcherListItem> findBySeason(PitcherSearchCondition cond, @PageableDefault(size = 20) Pageable pageable) {
        return pitcherQueryService.search(cond, pageable);
    }

    @GetMapping("/{kboPlayerId}")
    public PitcherDetailResponse findDetail(
            @PathVariable String kboPlayerId,
            @RequestParam Integer season) {
        return pitcherQueryService.findDetail(kboPlayerId, season);
    }
}
