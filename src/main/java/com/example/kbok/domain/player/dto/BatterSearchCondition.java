package com.example.kbok.domain.player.dto;

public record BatterSearchCondition(
    Integer season,
    String team,
    String name,
    Boolean qualified,
    BatterSort sort
) {
    public BatterSearchCondition{
        if(qualified == null){
            qualified = true;
        }

        if(sort == null){
            sort = BatterSort.OPS;
        }
    }
}
