package com.example.kbok.domain.player.ranking;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record LeaguePitchingTotals(
    Long earnedRuns,
    Long outs,
    Long bb,
    Long hits
) {
    private static final int SCALE = 2;

    public BigDecimal era(){
        if (earnedRuns == null){
            return null;
        }
        return divide(earnedRuns * 27, outs);
    }

    public BigDecimal whip(){
        if(bb == null || hits == null){
            return null;
        }
        return divide((bb + hits) * 3, outs);
    }

    private BigDecimal divide(long numerator, Long denominator){
        if (denominator == null || denominator == 0){
            return null;
        }
        return BigDecimal.valueOf(numerator)
            .divide(BigDecimal.valueOf(denominator), SCALE, RoundingMode.HALF_UP);
    }
}
