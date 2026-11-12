package com.example.kbok.crawler.kbo.mapper;

import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.FieldingStatDto;
import com.example.kbok.domain.player.entity.FieldingStat;
import com.example.kbok.domain.player.entity.Player;
import com.example.kbok.domain.player.repository.FieldingStatRepository;
import com.example.kbok.domain.player.repository.PlayerRepository;
import com.example.kbok.domain.team.entity.Team;
import com.example.kbok.domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FieldingStatMapper {

    private final FieldingStatRepository fieldingStatRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public FieldingStat upsert(FieldingStatDto dto, Integer season) {
        Team team = teamRepository.findByName(dto.getTeam())
            .orElseThrow(() -> new IllegalStateException("[FieldingStat] 팀 없음: " + dto.getTeam()));
        String playerId = String.valueOf(dto.getPlayerId());
        Player player = playerRepository.findByKboPlayerId(playerId)
            .orElseThrow(() -> new IllegalStateException("[FieldingStat] 선수 없음: " + dto.getName()));

        int games = dto.getGames() != null ? dto.getGames() : 0;
        int errors = dto.getErrors() != null ? dto.getErrors() : 0;

        return fieldingStatRepository
            .findByPlayerKboPlayerIdAndSeasonAndPosition(playerId, season, dto.getPosition())
            .map(existing -> {
                existing.updateFieldingStat(team, games, errors, dto.getFieldingPct());
                return fieldingStatRepository.save(existing);   // 변경 감지에 기대지 않고 명시적으로 저장
            })
            .orElseGet(() -> fieldingStatRepository.save(
                FieldingStat.builder()
                    .player(player)
                    .team(team)
                    .season(season)
                    .position(dto.getPosition())
                    .games(games)
                    .errors(errors)
                    .fieldingPct(dto.getFieldingPct())
                    .build()
            ));
    }
}
