package com.example.kbok.crawler.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PitcherStatBasic2Dto {
    private Long playerId;

    private String name;
    private String team;

    private Integer cg;         // 완투
    private Integer sho;        // 완봉
    private Integer qs;         // 퀄리티 스타트
    private Integer bsv;        // 블론세이브
    private Integer tbf;        // 타자수
    private Integer doubles;    // 2루타
    private Integer triples;    // 3루타
    private Integer ibb;        // 고의4구
    private Integer wp;         // 폭투
    private Integer bk;         // 보크
}
