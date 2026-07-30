package com.example.kbok.domain.player.entity;

import java.math.BigDecimal;

import com.example.kbok.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "batter_stat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BatterStat {

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

    @Column(nullable = false)
    private BigDecimal slg;

    @Column(nullable = false)
    private BigDecimal obp;

    @Column(nullable =  false)
    private BigDecimal ops;

    @Builder
    public BatterStat(Player player, Team team, Integer season, Integer games,
                      Integer pa, Integer ab, Integer runs, Integer hits,
                      Integer doubles, Integer triples, Integer homeRuns,
                      Integer totalBases, Integer rbi, Integer sac, Integer sf, Integer bb, Integer ibb,
                      Integer hbp, Integer so, Integer gdp, BigDecimal slg, BigDecimal obp, BigDecimal ops) {
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
    }

    public void updateBatterStat(Player player, Integer games,
                      Integer pa, Integer ab, Integer runs, Integer hits,
                      Integer doubles, Integer triples, Integer homeRuns,
                      Integer totalBases, Integer rbi, Integer sac, Integer sf, Integer bb, Integer ibb,
                      Integer hbp, Integer so, Integer gdp, BigDecimal slg, BigDecimal obp, BigDecimal ops){
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
    }
}