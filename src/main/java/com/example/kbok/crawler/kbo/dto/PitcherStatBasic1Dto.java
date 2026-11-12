package com.example.kbok.crawler.kbo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PitcherStatBasic1Dto {
    private Long playerId;

    private String name;
    private String team;

    private java.math.BigDecimal era;   // 평균자책점 (KBO 공식 값)
    private java.math.BigDecimal whip;  // WHIP (KBO 공식 값)
    private Integer game;   // 경기
    private Integer w;      // 승리
    private Integer l;      // 패배
    private Integer sv;     // 세이브
    private Integer hld;    // 홀드
    private String ip;      // 이닝
    private Integer h;      // 피안타
    private Integer hr;     // 피홈런
    private Integer bb;     // 볼넷
    private Integer hbp;    // 사구
    private Integer so;     // 삼진
    private Integer r;      // 실점
    private Integer er;     // 자책점

}
