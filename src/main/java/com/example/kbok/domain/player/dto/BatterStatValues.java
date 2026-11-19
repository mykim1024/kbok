package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;

import com.example.kbok.domain.player.ranking.BattingStandard;

public record BatterStatValues(
    Integer pa,
    BigDecimal avg,
    BigDecimal obp,
    BigDecimal slg,
    BigDecimal ops,
    Integer homeRuns
) {
    public boolean qualified() {
        return pa != null && pa >= BattingStandard.QUALIFIED_PA;
    }
}
