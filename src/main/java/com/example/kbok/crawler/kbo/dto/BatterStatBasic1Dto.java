package com.example.kbok.crawler.kbo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatterStatBasic1Dto {
    private Long playerId;

    private String name;
    private String team;

    private Integer game;        // 경기
    private Integer pa;          // 타석
    private Integer ab;          // 타수
    private Integer run;         // 득점
    private Integer hit;         // 안타
    private Integer double2b;    // 2루타
    private Integer triple3b;    // 3루타
    private Integer hr;          // 홈런
    private Integer tb;          // 루타
    private Integer rbi;         // 타점
    private Integer sac;         // 희생번트
    private Integer sf;          // 희생플라이
}
