package com.example.kbok.domain.player.dto;

public enum PitcherSort {
    ERA(true),
    WHIP(true),
    WINS(false),
    SO(false),
    SAVES(false),
    HOLDS(false);

    private final boolean rateStat;

    PitcherSort(boolean rateStat){
        this.rateStat = rateStat;
    }

    public boolean isRateStat(){
        return rateStat;
    }
}
