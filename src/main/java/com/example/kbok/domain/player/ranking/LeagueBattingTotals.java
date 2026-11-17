package com.example.kbok.domain.player.ranking;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record LeagueBattingTotals(
    Long hits,
    Long ab,
    Long bb,
    Long hbp,
    Long sf,
    Long totalBases
) {
    private static final int SCALE = 3;

    public BigDecimal avg(){
        return divide(hits, ab);
    }

    public BigDecimal obp() {
        return divide(hits + bb + hbp, ab + bb + hbp + sf);
    }

    public BigDecimal slg() {
        return divide(totalBases, ab);
    }

    public BigDecimal ops() {
        BigDecimal obp = obp();
        BigDecimal slg = slg();
        if (obp == null || slg == null) {
            return null;
        }
        return obp.add(slg);
    }

        private BigDecimal divide(Long numerator, Long denominator) {
        if (numerator == null || denominator == null || denominator == 0) {
            return null;
        }
        return BigDecimal.valueOf(numerator)
                .divide(BigDecimal.valueOf(denominator), SCALE, RoundingMode.HALF_UP);
    }

}
