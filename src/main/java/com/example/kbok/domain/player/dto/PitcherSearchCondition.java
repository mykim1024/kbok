package com.example.kbok.domain.player.dto;

import org.springframework.util.StringUtils;

public record PitcherSearchCondition(
    Integer season,
    String team,
    String name,
    PitcherRole role,
    Boolean qualified,
    PitcherSort sort
) {
    public PitcherSearchCondition{
        if(sort == null){
            sort = role == PitcherRole.RELIEVER ? PitcherSort.SAVES : PitcherSort.ERA;
        }

        if(qualified == null){
            qualified = sort.isRateStat()
                && role != PitcherRole.RELIEVER
                && !StringUtils.hasText(name);
        }
    }
}
