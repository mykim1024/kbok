package com.example.kbok.domain.player.popular;

import java.util.Optional;

public record ViewCount(
    PlayerType type,
    String kboPlayerId,
    long views
) {
    public static Optional<ViewCount> parse(String member, Double score){
        if(member == null){
            return Optional.empty();
        }

        String[] parts = member.split(":", 2);
        if(parts.length != 2){
            return Optional.empty();
        }

        try {
            PlayerType type = PlayerType.valueOf(parts[0]);
            long views = score == null ? 0 : score.longValue();
            return Optional.of(new ViewCount(type, parts[1], views));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static ViewCount from(String member, Double score){
        String[] parts = member.split(":", 2);
        return new ViewCount(
            PlayerType.valueOf(parts[0]), 
            parts[1], 
            score == null ? 0 : score.longValue());
    }

}
