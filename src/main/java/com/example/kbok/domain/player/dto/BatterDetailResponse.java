package com.example.kbok.domain.player.dto;

import java.util.List;

public record BatterDetailResponse(
        String kboPlayerId,
        String name,
        String team,
        String position,
        Integer season,
        Integer pa,
        boolean qualified,
        int qualifiedPa,
        List<StatInterpretation> stat
) {}
