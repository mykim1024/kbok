package com.example.kbok.domain.player.dto;

import org.springframework.util.StringUtils;

public record BatterSearchCondition(
    Integer season,
    String team,
    String name,
    Boolean qualified,
    BatterSort sort
) {
    public BatterSearchCondition{
        if(qualified == null){
            qualified = !StringUtils.hasText(name);
        }

        if(sort == null){
            sort = BatterSort.OPS;
        }
    }
}
