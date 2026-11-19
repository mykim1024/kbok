package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;

public record BatterDetailRow(
    String kboPlayerId,
    String name,
    String team,
    String position,
    Integer pa,
    BigDecimal avg,
    BigDecimal obp,
    BigDecimal slg,
    BigDecimal ops,
    Integer homeRuns
) {
    public BatterStatValues values() {
        return new BatterStatValues(pa, avg, obp, slg, ops, homeRuns);
    }
}
