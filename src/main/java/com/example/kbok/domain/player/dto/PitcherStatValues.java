package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;

import com.example.kbok.domain.player.ranking.PitchingStandard;

public record PitcherStatValues(
    Integer outs,
    BigDecimal era,
    BigDecimal whip,
    Integer wins,
    Integer so,
    Integer saves,
    Integer holds
) {
    public boolean qualified(){
        return outs != null && outs >= PitchingStandard.QUALIFIED_OUTS;
    }
}
