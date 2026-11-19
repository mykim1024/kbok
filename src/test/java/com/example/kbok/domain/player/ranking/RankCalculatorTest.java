package com.example.kbok.domain.player.ranking;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class RankCalculatorTest {

    @Test
    void 가장_높으면_1위(){
        List<BigDecimal> ops = decimals("0.950", "0.900", "0.800");

        Rank rank = RankCalculator.rankOf(new BigDecimal("0.950"), ops);

        assertThat(rank.rank()).isEqualTo(1);
        assertThat(rank.total()).isEqualTo(3);
    }

    @Test
    void 동점이면_같은_순위이고_다음_순위는_건너뛴다() {
        List<BigDecimal> ops = decimals("0.950", "0.900", "0.900", "0.800");

        assertThat(RankCalculator.rankOf(new BigDecimal("0.900"), ops).rank()).isEqualTo(2);
        assertThat(RankCalculator.rankOf(new BigDecimal("0.800"), ops).rank()).isEqualTo(4);
    }

    @Test
    void 소수점_자릿수가_달라도_같은_값이면_동점() {
        List<BigDecimal> avg = decimals("0.310", "0.30", "0.300");

        assertThat(RankCalculator.rankOf(new BigDecimal("0.300"), avg).rank()).isEqualTo(2);
    }

    @Test
    void 홈런처럼_정수_기록도_계산된다() {
        List<Integer> homeRuns = List.of(38, 33, 33, 20);

        assertThat(RankCalculator.rankOf(33, homeRuns).rank()).isEqualTo(2);
    }

    @Test
    void 상위_퍼센트는_올림한다() {
        assertThat(new Rank(8, 61).topPercent()).isEqualTo(14);
        assertThat(new Rank(1, 500).topPercent()).isEqualTo(1);
        assertThat(new Rank(61, 61).topPercent()).isEqualTo(100);
    }

    @Test
    void 낮을수록_좋은_기록은_낮은_값이_1위() {
        List<BigDecimal> era = decimals("2.10", "3.00", "3.00", "4.50");

        assertThat(RankCalculator.rankOf(new BigDecimal("2.10"), era, RankDirection.LOWER_IS_BETTER).rank()).isEqualTo(1);
        assertThat(RankCalculator.rankOf(new BigDecimal("3.00"), era, RankDirection.LOWER_IS_BETTER).rank()).isEqualTo(2);
        assertThat(RankCalculator.rankOf(new BigDecimal("4.50"), era, RankDirection.LOWER_IS_BETTER).rank()).isEqualTo(4);
    }

    private List<BigDecimal> decimals(String... values) {
        return Arrays.stream(values).map(BigDecimal::new).toList();
    }
}
