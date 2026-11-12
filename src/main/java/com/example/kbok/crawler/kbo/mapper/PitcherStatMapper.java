package com.example.kbok.crawler.kbo.mapper;

import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.PitcherStatDto;
import com.example.kbok.domain.player.entity.PitcherStat;
import com.example.kbok.domain.player.entity.Player;
import com.example.kbok.domain.player.repository.PitcherStatRepository;
import com.example.kbok.domain.player.repository.PlayerRepository;
import com.example.kbok.domain.team.entity.Team;
import com.example.kbok.domain.team.repository.TeamRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PitcherStatMapper {
    
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PitcherStatRepository pitcherStatRepository;

    public PitcherStat upsert(PitcherStatDto dto){
        Team team = teamRepository.findByName(dto.getTeam())
            .orElseThrow(() -> new IllegalStateException("[PitcherStat] 팀 데이터 존재하지 않음."));
        
        String playerId = String.valueOf(dto.getPlayerId());
        Player player = playerRepository.findByKboPlayerId(playerId)
            .orElseThrow(() -> new IllegalStateException("[PitcherStat] 선수 데이터 존재하지 않음."));
    
        return pitcherStatRepository.findByPlayerKboPlayerIdAndSeason(playerId, dto.getSeason())
            .map(existing -> {
                existing.updatePitcherStat(player, dto.getGame(), dto.getW(), dto.getL(), dto.getSv(),
                    dto.getHld(), dto.getBsv(), dto.getCg(), dto.getSho(), dto.getQs(), dto.getIp(),
                    dto.getTbf(), dto.getH(), dto.getHr(), dto.getDoubles(), dto.getTriples(),
                    dto.getBb(), dto.getIbb(), dto.getHbp(), dto.getSo(), dto.getR(), 
                    dto.getEr(), dto.getWp(), dto.getBk(),
                    dto.getEra(), dto.getWhip());
                // 서비스 내부 호출이라 @Transactional이 적용되지 않아 변경 감지가 동작하지 않는다 → 명시적으로 저장
                return pitcherStatRepository.save(existing);
            })
            .orElseGet(() -> pitcherStatRepository.save(
                PitcherStat.builder()
                    .player(player)
                    .team(team)
                    .season(dto.getSeason())
                    .games(dto.getGame())
                    .wins(dto.getW())
                    .losses(dto.getL())
                    .saves(dto.getSv())
                    .holds(dto.getHld())
                    .bsv(dto.getBsv())
                    .cg(dto.getCg())
                    .sho(dto.getSho())
                    .qs(dto.getQs())
                    .inningsPitched((dto.getIp()))
                    .tbf(dto.getTbf())
                    .hits(dto.getH())
                    .homeRuns(dto.getHr())
                    .doubles(dto.getDoubles())
                    .triples(dto.getTriples())
                    .bb(dto.getBb())
                    .ibb(dto.getIbb())
                    .hbp(dto.getHbp())
                    .so(dto.getSo())
                    .runs(dto.getR())
                    .earnedRuns(dto.getEr())
                    .wildPitches(dto.getWp())
                    .balks(dto.getBk())
                    .era(dto.getEra())
                    .whip(dto.getWhip())
                    .build()
            ));
        
    }
}
