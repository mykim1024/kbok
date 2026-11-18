package com.example.kbok.domain.player.dto;

public enum PitcherRole {
    STARTER, RELIEVER;

    public static PitcherRole of(int games, int gamesStarted){
        return gamesStarted * 2 >= games ? STARTER : RELIEVER;
    }
}
