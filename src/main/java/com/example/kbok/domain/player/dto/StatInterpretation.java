package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;

public record StatInterpretation(
    String label,
    Number value,
    Integer rank,
    Integer total,
    Integer topPercent,
    BigDecimal leagueAverage
) {}
