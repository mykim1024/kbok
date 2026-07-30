package com.example.kbok.domain.team.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team_rank")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamRank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer season;

    @Column(nullable = false)
    private Integer rank;

    @Column(length = 20)
    private String postseason;  // 한국시리즈/플옵/준플옵/와카/없음

    @Column(nullable = false)
    private Boolean champion;

    @Builder
    public TeamRank(Team team, Integer season, Integer rank,
                    String postseason, Boolean champion) {
        this.team = team;
        this.season = season;
        this.rank = rank;
        this.postseason = postseason;
        this.champion = champion;
    }
}