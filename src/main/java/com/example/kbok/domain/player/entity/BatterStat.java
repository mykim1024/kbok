package com.example.kbok.domain.player.entity;

import java.math.BigDecimal;

import com.example.kbok.domain.common.entity.BaseEntity;
import com.example.kbok.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batter_stat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatterStat extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer season;

    @Column(nullable = false)
    private Integer games;

    @Column(nullable = false)
    private Integer pa;

    @Column(nullable = false)
    private Integer ab;

    @Column(nullable = false)
    private Integer runs;

    @Column(nullable = false)
    private Integer hits;

    @Column(nullable = false)
    private Integer doubles;

    @Column(nullable = false)
    private Integer triples;

    @Column(nullable = false)
    private Integer homeRuns;

    @Column(nullable = false)
    private Integer totalBases;

    @Column(nullable = false)
    private Integer rbi;

    @Column(nullable = false)
    private Integer sac;

    @Column(nullable = false)
    private Integer sf;


    @Column(nullable = false)
    private Integer bb;

    @Column(nullable = false)
    private Integer ibb;

    @Column(nullable = false)
    private Integer hbp;

    @Column(nullable = false)
    private Integer so;

    @Column(nullable = false)
    private Integer gdp;

    // 비율 기록은 분모가 0이면 KBO가 "-"로 표시한다 (타수 0, 희생번트만 있는 타석 등). 계산 불가라 null 허용.
    private BigDecimal slg;

    private BigDecimal obp;

    private BigDecimal ops;

    private BigDecimal avg;     // 타율 (KBO 공식 값)

    @Column(nullable = false)
    private Integer sb;         // 도루 (주루 크롤러에서 채움)

    @Column(nullable = false)
    private Integer cs;         // 도루 실패 (주루 크롤러에서 채움)

    @Builder
    public BatterStat(Player player, Team team, Integer season, Integer games,
                      Integer pa, Integer ab, Integer runs, Integer hits,
                      Integer doubles, Integer triples, Integer homeRuns,
                      Integer totalBases, Integer rbi, Integer sac, Integer sf, Integer bb, Integer ibb,
                      Integer hbp, Integer so, Integer gdp, BigDecimal slg, BigDecimal obp, BigDecimal ops,
                      BigDecimal avg) {
        this.player = player;
        this.team = team;
        this.season = season;
        this.games = games;
        this.pa = pa;
        this.ab = ab;
        this.runs = runs;
        this.hits = hits;
        this.doubles = doubles;
        this.triples = triples;
        this.homeRuns = homeRuns;
        this.totalBases = totalBases;
        this.rbi = rbi;
        this.sac = sac;
        this.sf = sf;

        this.bb = bb;
        this.ibb = ibb;
        this.hbp = hbp;
        this.so = so;
        this.gdp = gdp;
        this.slg = slg;
        this.obp = obp;
        this.ops = ops;
        this.avg = avg;
        this.sb = 0;   // 주루 기록은 별도 수집 전까지 0
        this.cs = 0;
    }

    public void updateBatterStat(Player player, Integer games,
                      Integer pa, Integer ab, Integer runs, Integer hits,
                      Integer doubles, Integer triples, Integer homeRuns,
                      Integer totalBases, Integer rbi, Integer sac, Integer sf, Integer bb, Integer ibb,
                      Integer hbp, Integer so, Integer gdp, BigDecimal slg, BigDecimal obp, BigDecimal ops,
                      BigDecimal avg){
        this.player = player;
        this.games = games;
        this.pa = pa;
        this.ab = ab;
        this.runs = runs;
        this.hits = hits;
        this.doubles = doubles;
        this.triples =triples;
        this.homeRuns = homeRuns;
        this.totalBases = totalBases;
        this.rbi = rbi;
        this.sac = sac;
        this.sf = sf;

        this.bb = bb;
        this.ibb = ibb;
        this.hbp = hbp;
        this.so = so;
        this.gdp = gdp;
        this.slg = slg;
        this.obp = obp;
        this.ops = ops;
        this.avg = avg;
    }

    /** 주루 기록(도루·도루 실패) 갱신 */
    public void updateRunning(Integer sb, Integer cs) {
        this.sb = sb;
        this.cs = cs;
    }
}