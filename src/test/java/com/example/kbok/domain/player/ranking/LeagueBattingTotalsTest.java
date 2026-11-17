package com.example.kbok.domain.player.ranking;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class LeagueBattingTotalsTest {
    @Test
    void 리그_타율은_타율의_평균이_아니라_합계로_계산한다() {
        // A: 1안타/1타수(1.000), B: 29안타/99타수(0.293) → 합계 30/100
        LeagueBattingTotals totals = new LeagueBattingTotals(30L, 100L, 0L, 0L, 0L, 30L);

        assertThat(totals.avg()).isEqualByComparingTo("0.300");
    }

    @Test
    void 출루율은_볼넷_사구_희생플라이를_반영한다() {
        // (25 + 10 + 2) / (90 + 10 + 2 + 3) = 37 / 105 = 0.3523...
        LeagueBattingTotals totals = new LeagueBattingTotals(25L, 90L, 10L, 2L, 3L, 40L);

        assertThat(totals.obp()).isEqualByComparingTo("0.352");
    }

    @Test
    void 타수가_0이면_null() {
        LeagueBattingTotals totals = new LeagueBattingTotals(0L, 0L, 0L, 0L, 0L, 0L);

        assertThat(totals.avg()).isNull();
    }
}
