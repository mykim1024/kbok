package com.example.kbok.crawler.kbo.mapper;

import java.util.Map;

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

    /**
     * 선수 명단의 팀 이름 → 팀 테이블 이름.
     * 명단은 2군까지 포함한 구단 전체 소속 선수인데, 다른 구단 2군은 모구단 이름으로 나오는 반면
     * 키움 2군만 연고지 이름("고양", 고양 히어로즈)으로 나온다. 같은 구단 소속이므로 키움으로 맞춘다.
     */
    private static final Map<String, String> TEAM_ALIAS = Map.of("고양", "키움");

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    public Player upsert(PlayerProfileDto dto){
        String teamName = TEAM_ALIAS.getOrDefault(dto.getTeamName(), dto.getTeamName());
        Team team = teamRepository.findByName(teamName)
                    .orElseThrow(() -> new IllegalStateException(
                        "Team 없음: " + dto.getTeamName() + " (10개 팀 시드 데이터 확인 필요)"));
        
        String kboPlayerId = String.valueOf(dto.getKboPlayerId());

        return playerRepository.findByKboPlayerId(kboPlayerId)
            .map(existing -> {
                    existing.updateProfile(dto.getPosition(), dto.getBirthDate(),
                        dto.getHeight(), dto.getWeight(), dto.getCareer(),
                        dto.getBackNumber(), team);
                    // 기록 기준으로 만들어져 비활성이던 선수가 명단에 나타나면 다시 활성화
                    existing.activate();
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
