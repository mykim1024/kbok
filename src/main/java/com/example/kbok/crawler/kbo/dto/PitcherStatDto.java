package com.example.kbok.crawler.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PitcherStatDto {
    private Long playerId;

    private String name;
    private String team;
    private Integer season;

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

    private Integer gs;     // 선발
    private Integer svo;    // 세이브 기회
    private Integer gdp;    // 병살타

    private java.math.BigDecimal era;   // 평균자책점
    private java.math.BigDecimal whip;  // WHIP

    public static PitcherStatDto merge(Integer season, PitcherStatBasic1Dto basic1, PitcherStatBasic2Dto basic2, PitcherStatDetailDto detail){
        return PitcherStatDto.builder()
            .playerId(basic1.getPlayerId())
            .name(basic1.getName())
            .team(basic1.getTeam())
            .season(season)
            .game(basic1.getGame())
            .w(basic1.getW())
            .l(basic1.getL())
            .sv(basic1.getSv())
            .hld(basic1.getHld())
            .ip(basic1.getIp())
            .h(basic1.getH())
            .hr(basic1.getHr())
            .bb(basic1.getBb())
            .hbp(basic1.getHbp())
            .so(basic1.getSo())
            .r(basic1.getR())
            .er(basic1.getEr())
            .cg(basic2.getCg())
            .sho(basic2.getSho())
            .qs(basic2.getQs())
            .bsv(basic2.getBsv())
            .tbf(basic2.getTbf())
            .doubles(basic2.getDoubles())
            .triples(basic2.getTriples())
            .ibb(basic2.getIbb())
            .wp(basic2.getWp())
            .bk(basic2.getBk())
            .gs(detail.getGs())
            .svo(detail.getSvo())
            .gdp(detail.getGdp())
            .era(basic1.getEra())
            .whip(basic1.getWhip())
            .build();
    }
}
