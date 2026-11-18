package com.example.kbok.domain.player.ranking;

import java.util.List;
import java.util.Objects;

public class RankCalculator {
    private RankCalculator() {}

    public static <T extends Comparable<T>> Rank rankOf(T target, List<T> values){
        return rankOf(target, values, RankDirection.HIGHER_IS_BETTER);
    }

    // 타율, 출루율, 장타율, OPS BigDecimal
    // 홈런 Integer
    public static <T extends Comparable<T>> Rank rankOf(T target, List<T> values, RankDirection direction) {
        Objects.requireNonNull(target, "순위를 매길 기록이 없습니다");

        //null 제거
        List<T> valid = values.stream()
                .filter(Objects::nonNull)
                .toList();

        long higher = valid.stream()
                .filter(v -> isBetter(v, target, direction))
                .count();

        return new Rank((int) higher + 1, valid.size());
    }

    public static <T extends Comparable<T>> boolean isBetter(T value, T target, RankDirection direction){
        int result = value.compareTo(target);
        return switch (direction) {
                case HIGHER_IS_BETTER -> result > 0;
                case LOWER_IS_BETTER -> result < 0;
            };
    }
    
}
