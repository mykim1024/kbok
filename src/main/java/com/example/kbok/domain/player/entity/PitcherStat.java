package com.example.kbok.domain.player.entity;

import com.example.kbok.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pitcher_stat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PitcherStat {

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
    private Integer gamesStarted;

    @Column(nullable = false)
    private Integer wins;

    @Column(nullable = false)
    private Integer losses;

    @Column(nullable = false)
    private Integer saves;

    @Column(nullable = false)
    private Integer holds;

    @Column(nullable = false)
    private Integer bsv;

    @Column(nullable = false)
    private Integer svo;

    @Column(nullable = false)
    private Integer cg;

    @Column(nullable = false)
    private Integer sho;

    @Column(nullable = false)
    private Integer qs;

    @Column(nullable = false)
    private String inningsPitched;

    @Column(nullable = false)
    private Integer tbf;

    @Column(nullable = false)
    private Integer hits;

    @Column(nullable = false)
    private Integer homeRuns;

    @Column(nullable = false)
    private Integer doubles;

    @Column(nullable = false)
    private Integer triples;

    @Column(nullable = false)
    private Integer bb;

    @Column(nullable = false)
    private Integer ibb;

    @Column(nullable = false)
    private Integer hbp;

    @Column(nullable = false)
    private Integer so;

    @Column(nullable = false)
    private Integer runs;

    @Column(nullable = false)
    private Integer earnedRuns;

    @Column(nullable = false)
    private Integer wildPitches;

    @Column(nullable = false)
    private Integer balks;

    @Column(nullable = false)
    private Integer gdp;

    @Builder
    public PitcherStat(Player player, Team team, Integer season, Integer games,
                       Integer gamesStarted, Integer wins, Integer losses,
                       Integer saves, Integer holds, Integer bsv, Integer svo,
                       Integer cg, Integer sho, Integer qs, String inningsPitched,
                       Integer tbf, Integer hits, Integer homeRuns, Integer doubles,
                       Integer triples, Integer bb, Integer ibb, Integer hbp,
                       Integer so, Integer runs, Integer earnedRuns,
                       Integer wildPitches, Integer balks, Integer gdp) {
        this.player = player;
        this.team = team;
        this.season = season;
        this.games = games;
        this.gamesStarted = gamesStarted;
        this.wins = wins;
        this.losses = losses;
        this.saves = saves;
        this.holds = holds;
        this.bsv = bsv;
        this.svo = svo;
        this.cg = cg;
        this.sho = sho;
        this.qs = qs;
        this.inningsPitched = inningsPitched;
        this.tbf = tbf;
        this.hits = hits;
        this.homeRuns = homeRuns;
        this.doubles = doubles;
        this.triples = triples;
        this.bb = bb;
        this.ibb = ibb;
        this.hbp = hbp;
        this.so = so;
        this.runs = runs;
        this.earnedRuns = earnedRuns;
        this.wildPitches = wildPitches;
        this.balks = balks;
        this.gdp = gdp;
    }

    public void updatePitcherStat(Player player, Integer games,
                       Integer gamesStarted, Integer wins, Integer losses,
                       Integer saves, Integer holds, Integer bsv, Integer svo,
                       Integer cg, Integer sho, Integer qs, String inningsPitched,
                       Integer tbf, Integer hits, Integer homeRuns, Integer doubles,
                       Integer triples, Integer bb, Integer ibb, Integer hbp,
                       Integer so, Integer runs, Integer earnedRuns,
                       Integer wildPitches, Integer balks, Integer gdp) {
        this.player = player;
        this.games = games;
        this.gamesStarted = gamesStarted;
        this.wins = wins;
        this.losses = losses;
        this.saves = saves;
        this.holds = holds;
        this.bsv = bsv;
        this.svo = svo;
        this.cg = cg;
        this.sho = sho;
        this.qs = qs;
        this.inningsPitched = inningsPitched;
        this.tbf = tbf;
        this.hits = hits;
        this.homeRuns = homeRuns;
        this.doubles = doubles;
        this.triples = triples;
        this.bb = bb;
        this.ibb = ibb;
        this.hbp = hbp;
        this.so = so;
        this.runs = runs;
        this.earnedRuns = earnedRuns;
        this.wildPitches = wildPitches;
        this.balks = balks;
        this.gdp = gdp;
    }
}