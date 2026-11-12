package com.example.kbok.crawler.kbo.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

/** KBO 기록실 수비기록 한 줄 (선수·포지션 단위) */
@Data
@NoArgsConstructor
public class FieldingStatDto {
    private Long playerId;

    private String name;
    private String team;

    private String position;        // 포지션
    private Integer games;          // 출장 경기
    private Integer errors;         // 실책
    private BigDecimal fieldingPct; // 수비율
}
