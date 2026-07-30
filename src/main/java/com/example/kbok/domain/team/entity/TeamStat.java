package com.example.kbok.domain.team.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "team_stat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer season;

    @Column(nullable = false)
    private Integer games;

    @Column(nullable = false)
    private Integer wins;

    @Column(nullable = false)
    private Integer losses;

    @Column(nullable = false)
    private Integer draws;

    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal winPct;

    @Column(nullable = false)
    private Integer runsScored;

    @Column(nullable = false)
    private Integer runsAllowed;

    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal teamAvg;

    @Column(nullable = false)
    private Integer teamHr;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal teamEra;

    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal teamWhip;

    @Builder
    public TeamStat(Team team, Integer season, Integer games, Integer wins,
                    Integer losses, Integer draws, BigDecimal winPct,
                    Integer runsScored, Integer runsAllowed, BigDecimal teamAvg,
                    Integer teamHr, BigDecimal teamEra, BigDecimal teamWhip) {
        this.team = team;
        this.season = season;
        this.games = games;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.winPct = winPct;
        this.runsScored = runsScored;
        this.runsAllowed = runsAllowed;
        this.teamAvg = teamAvg;
        this.teamHr = teamHr;
        this.teamEra = teamEra;
        this.teamWhip = teamWhip;
    }
}