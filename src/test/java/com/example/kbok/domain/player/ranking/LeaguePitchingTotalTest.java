package com.example.kbok.domain.player.ranking;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class LeaguePitchingTotalTest {

    @Test
    void ERA는_자책점_곱하기_27_나누기_아웃() {
        // 30이닝(90아웃) 10자책 → 10 × 9 ÷ 30 = 3.00
        LeaguePitchingTotals totals = new LeaguePitchingTotals(10L, 90L, 0L, 0L);

        assertThat(totals.era()).isEqualByComparingTo("3.00");
    }

    @Test
    void 이닝에_3분의_1이_있어도_아웃으로_정확히_계산한다() {
        // 150 2/3이닝 = 452아웃, 50자책 → 50 × 27 ÷ 452 = 2.9867... → 2.99
        LeaguePitchingTotals totals = new LeaguePitchingTotals(50L, 452L, 0L, 0L);

        assertThat(totals.era()).isEqualByComparingTo("2.99");
    }

    @Test
    void WHIP는_볼넷과_피안타를_더해_이닝으로_나눈다() {
        // 30이닝(90아웃), 볼넷 10 + 피안타 35 = 45 → 45 ÷ 30 = 1.50
        LeaguePitchingTotals totals = new LeaguePitchingTotals(0L, 90L, 10L, 35L);

        assertThat(totals.whip()).isEqualByComparingTo("1.50");
    }

    @Test
    void 아웃이_0이면_null() {
        LeaguePitchingTotals totals = new LeaguePitchingTotals(0L, 0L, 0L, 0L);

        assertThat(totals.era()).isNull();
        assertThat(totals.whip()).isNull();
    }
    
}
