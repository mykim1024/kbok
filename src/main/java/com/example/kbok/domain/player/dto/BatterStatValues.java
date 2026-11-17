package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;

public record BatterStatValues(
    BigDecimal avg,
    BigDecimal obp,
    BigDecimal slg,
    BigDecimal ops,
    Integer homeRuns
) {
}
