package com.example.kbok.crawler.kbo.mapper;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.BatterStatDto;
import com.example.kbok.domain.player.entity.BatterStat;
import com.example.kbok.domain.player.entity.Player;
import com.example.kbok.domain.player.repository.BatterStatRepository;
import com.example.kbok.domain.player.repository.PlayerRepository;
import com.example.kbok.domain.team.entity.Team;
import com.example.kbok.domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatterStatMapper  {

    private final BatterStatRepository batterStatRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public BatterStat upsert(BatterStatDto dto){
        Team team = teamRepository.findByName(dto.getTeam())
                        .orElseThrow(() -> new IllegalStateException(
                            "Team 없음: " + dto.getTeam() + " (10개 팀 시드 데이터 확인 필요"));
        String playerId = String.valueOf(dto.getPlayerId());
        Player player = playerRepository.findByKboPlayerId(playerId)
                            .orElseThrow(() -> new IllegalStateException(
                                "Player 없음 ID: " + dto.getPlayerId() + " 이름: " + dto.getName() + "(선수 확인 필요)"
                            ));
        

        return batterStatRepository.findByPlayerKboPlayerIdAndSeason(playerId, dto.getSeason())
            .map(existing -> {
                    existing.updateBatterStat(player, dto.getGame(), dto.getPa(), dto.getAb(), dto.getRun(), dto.getHit(),
                        dto.getDouble2b(), dto.getTriple3b(), dto.getHr(), dto.getTb(), dto.getRbi(), dto.getSac(), dto.getSf(), dto.getBb(),
                        dto.getIbb(), dto.getHbp(), dto.getSo(), dto.getGdp(), dto.getSlg(), dto.getObp(), dto.getOps());
                return existing;
            })
            .orElseGet(() -> batterStatRepository.save(
                BatterStat.builder()
                    .player(player)
                    .team(team)
                    .season(dto.getSeason())
                    .games(dto.getGame())
                    .pa(dto.getPa())
                    .ab(dto.getAb())
                    .runs(dto.getRun())
                    .hits(dto.getHit())
                    .doubles(dto.getDouble2b())
                    .triples(dto.getTriple3b())
                    .homeRuns(dto.getHr())
                    .totalBases(dto.getTb())
                    .rbi(dto.getRbi())
                    .sac(dto.getSac())
                    .sf(dto.getSf())
                    .bb(dto.getBb())
                    .ibb(dto.getIbb())
                    .hbp(dto.getHbp())
                    .so(dto.getSo())
                    .gdp(dto.getGdp())
                    .slg(dto.getSlg())
                    .obp(dto.getObp())
                    .ops(dto.getOps())
                    .build()
            ));
    }
    
 }
