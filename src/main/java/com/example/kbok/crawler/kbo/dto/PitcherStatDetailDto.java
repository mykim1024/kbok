package com.example.kbok.crawler.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PitcherStatDetailDto {
    private Long playerId;

    private String name;
    private String team;

    private Integer gs;     // 선발
    private Integer svo;    // 세이브 기회
    private Integer gdp;    // 병살타

}
