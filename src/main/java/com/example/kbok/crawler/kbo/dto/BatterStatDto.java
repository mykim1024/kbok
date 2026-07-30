package com.example.kbok.crawler.kbo.dto;

import java.math.BigDecimal;

import com.example.kbok.domain.player.entity.BatterStat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatterStatDto {

    private Long playerId;

    private String name;
    private String team;
    private Integer season;
    
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

    private Integer bb;          // 볼넷
    private Integer ibb;         // 고의4구
    private Integer hbp;         // 사구
    private Integer so;          // 삼진
    private Integer gdp;         // 병살타
    private BigDecimal slg;         // 장타율
    private BigDecimal obp;         // 출루율
    private BigDecimal ops;         // OPS

    public BatterStatDto(Long playerId){
        this.playerId = playerId;
    }

    public static BatterStatDto merge(BatterStatBasic1Dto basic1, BatterStatBasic2Dto basic2, Integer season) {
        BatterStatDto dto = new BatterStatDto();
        dto.playerId = basic1.getPlayerId();
        dto.name = basic1.getName();
        dto.team = basic1.getTeam();
        dto.season = season;
        dto.game = basic1.getGame();
        dto.pa = basic1.getPa();
        dto.ab = basic1.getAb();
        dto.run = basic1.getRun();
        dto.hit = basic1.getHit();
        dto.double2b = basic1.getDouble2b();
        dto.triple3b = basic1.getTriple3b();
        dto.hr = basic1.getHr();
        dto.tb = basic1.getTb();
        dto.rbi = basic1.getRbi();
        dto.sac = basic1.getSac();
        dto.sf = basic1.getSf();
        dto.bb = basic2.getBb();
        dto.ibb = basic2.getIbb();
        dto.hbp = basic2.getHbp();
        dto.so = basic2.getSo();
        dto.gdp = basic2.getGdp();
        dto.slg = basic2.getSlg();
        dto.obp = basic2.getObp();
        dto.ops = basic2.getOps();
        return dto;
    }
}
