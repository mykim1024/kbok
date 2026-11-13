package com.example.kbok.crawler.kbo.mapper;

import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.FieldingStatDto;
import com.example.kbok.domain.player.entity.FieldingStat;
import com.example.kbok.domain.player.entity.Player;
import com.example.kbok.domain.player.repository.FieldingStatRepository;
import com.example.kbok.domain.team.entity.Team;
import com.example.kbok.domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FieldingStatMapper {

    private final FieldingStatRepository fieldingStatRepository;
    private final TeamRepository teamRepository;
    private final PlayerResolver playerResolver;

    public FieldingStat upsert(FieldingStatDto dto, Integer season) {
        Team team = teamRepository.findByName(dto.getTeam())
            .orElseThrow(() -> new IllegalStateException("[FieldingStat] 팀 없음: " + dto.getTeam()));
        String playerId = String.valueOf(dto.getPlayerId());
        // 명단에 없는 선수(시즌 중 방출·이적 등)도 기록이 버려지지 않게 기록 기준으로 만든다
        Player player = playerResolver.findOrCreate(dto.getPlayerId(), dto.getName());

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
