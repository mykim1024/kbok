package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record PitcherDetailRow(
    String kboPlayerId,
    String name,
    String team,
    Integer games,
    Integer gamesStarted,
    String inningsPitched,
    Integer outs,
    BigDecimal era,
    BigDecimal whip,
    Integer wins,
    Integer so,
    Integer saves,
    Integer holds
) {
    public PitcherStatValues values(){
        return new PitcherStatValues(outs, era, whip, wins, so, saves, holds);
    }

    public BigDecimal inningsPerGame(){
        if (outs == null || games == null || games == 0) {
            return null;
        }
        return BigDecimal.valueOf(outs)
            .divide(BigDecimal.valueOf(games * 3L), 1, RoundingMode.HALF_UP);
    }
}
