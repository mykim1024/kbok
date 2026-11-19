package com.example.kbok.domain.player.ranking;

public record Rank (int rank, int total) {
    public int topPercent(){
        return (int) Math.ceil(rank * 100.0 / total);
    }
}
