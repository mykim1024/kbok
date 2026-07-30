package com.example.kbok.crawler.kbo.mapper;

import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.PlayerProfileDto;
import com.example.kbok.domain.player.entity.Player;
import com.example.kbok.domain.player.repository.PlayerRepository;
import com.example.kbok.domain.team.entity.Team;
import com.example.kbok.domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerProfileMapper {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public Player upsert(PlayerProfileDto dto){
        Team team = teamRepository.findByName(dto.getTeamName())
                    .orElseThrow(() -> new IllegalStateException(
                        "Team 없음: " + dto.getTeamName() + " (10개 팀 시드 데이터 확인 필요)"));
        
        String kboPlayerId = String.valueOf(dto.getKboPlayerId());

        return playerRepository.findByKboPlayerId(kboPlayerId)
            .map(existing -> {
                    existing.updateProfile(dto.getPosition(), dto.getBirthDate(),
                        dto.getHeight(), dto.getWeight(), dto.getCareer(),
                        dto.getBackNumber(), team);
                    return existing;
        })
        .orElseGet(() -> playerRepository.save(
            Player.builder()
                .kboPlayerId(kboPlayerId)
                .name(dto.getName())
                .team(team)
                .position(dto.getPosition())
                .birthDate(dto.getBirthDate())
                .height(dto.getHeight())
                .weight(dto.getWeight())
                .school(dto.getCareer())
                .backNumber(dto.getBackNumber())
                .build()
        ));
    }
}
