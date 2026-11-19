package com.example.kbok.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.kbok.domain.player.popular.PopolarPlayer;
import com.example.kbok.domain.player.popular.PopularPlayerService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PopularPlayerController {
    
    private final PopularPlayerService popularPlayerService;

    @GetMapping("/popular")
    public List<PopolarPlayer> findTodayTop() {
        return popularPlayerService.findTodayTop();
    }
    
}
