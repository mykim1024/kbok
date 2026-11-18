package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;

public record PitcherListItem(
    String kboPlayerId,
    String name,
    String team,
    Integer games,
    Integer ganesStarted,
    String inningPitched,
    Integer wins,
    Integer losses,
    Integer saves,
    Integer holds,
    Integer so,
    BigDecimal era,
    BigDecimal whip
) {}
