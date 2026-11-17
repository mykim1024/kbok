package com.example.kbok.domain.player.ranking;

import java.util.List;
import java.util.Objects;

public class RankCalculator {
    private RankCalculator() {}

    // 타율, 출루율, 장타율, OPS BigDecimal
    // 홈런 Integer
    public static <T extends Comparable<T>> Rank rankOf(T target, List<T> values) {
        Objects.requireNonNull(target, "순위를 매길 기록이 없습니다");

        //null 제거
        List<T> valid = values.stream()
                .filter(Objects::nonNull)
                .toList();

        long higher = valid.stream()
                .filter(v -> v.compareTo(target) > 0)
                .count();

        return new Rank((int) higher + 1, valid.size());
    }
    
}
