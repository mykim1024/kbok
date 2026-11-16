package com.example.kbok.domain.player.dto;

import java.math.BigDecimal;

public record BatterListItem ( 
    String name,
    String team,
    Integer pa,
    Integer homeRuns,
    BigDecimal avg,
    BigDecimal ops
) {}
