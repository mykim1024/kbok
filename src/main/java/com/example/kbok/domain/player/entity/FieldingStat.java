package com.example.kbok.domain.player.entity;

import java.math.BigDecimal;

import com.example.kbok.domain.common.entity.BaseEntity;
import com.example.kbok.domain.team.entity.Team;

import jakarta.persistence.*;
import lombok.*;

/**
 * 수비 기록 (참고용). 포지션과 수비 범위에 따라 의미가 달라 백분위 해석은 하지 않는다.
 * 한 선수가 여러 포지션을 보면 (선수, 시즌, 포지션)마다 한 줄.
 */
@Entity
@Table(name = "fielding_stat")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FieldingStat extends BaseEntity {

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

    @Column(nullable = false, length = 10)
    private String position;

    @Column(nullable = false)
    private Integer games;

    @Column(nullable = false)
    private Integer errors;

    private BigDecimal fieldingPct;   // 수비 기회가 없으면 null

    @Builder
    public FieldingStat(Player player, Team team, Integer season, String position,
                        Integer games, Integer errors, BigDecimal fieldingPct) {
        this.player = player;
        this.team = team;
        this.season = season;
        this.position = position;
        this.games = games;
        this.errors = errors;
        this.fieldingPct = fieldingPct;
    }

    public void updateFieldingStat(Team team, Integer games, Integer errors, BigDecimal fieldingPct) {
        this.team = team;
        this.games = games;
        this.errors = errors;
        this.fieldingPct = fieldingPct;
    }
}
