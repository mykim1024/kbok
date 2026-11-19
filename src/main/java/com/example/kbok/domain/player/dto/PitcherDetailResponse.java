package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;
import java.util.List;

public record PitcherDetailResponse(
    String kboPlayerId,
    String name,
    String team,
    PitcherRole role,
    Integer season,
    Integer games,
    Integer gamesStarted,
    String inningsPitched,
    BigDecimal inningsPerGame,
    boolean qualified,
    int qualifiedInnings,
    List<StatInterpretation> stats
) {}
