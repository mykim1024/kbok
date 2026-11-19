package com.example.kbok.domain.player.popular;

public record PopolarPlayer(
    int rank,
    PlayerType type,
    String kboPlayerId,
    String name,
    String team,
    long views
) {}
