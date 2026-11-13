package com.example.kbok.crawler.kbo.mapper;

import org.springframework.stereotype.Component;

import com.example.kbok.crawler.kbo.dto.BatterStatDto;
import com.example.kbok.crawler.kbo.dto.RunnerStatDto;
import com.example.kbok.domain.player.entity.BatterStat;
import com.example.kbok.domain.player.entity.Player;
import com.example.kbok.domain.player.repository.BatterStatRepository;
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
    private final PlayerResolver playerResolver;

    public BatterStat upsert(BatterStatDto dto){
        Team team = teamRepository.findByName(dto.getTeam())
                        .orElseThrow(() -> new IllegalStateException(
                            "Team 없음: " + dto.getTeam() + " (10개 팀 시드 데이터 확인 필요"));
        String playerId = String.valueOf(dto.getPlayerId());
        // 명단에 없는 선수(시즌 중 방출·이적 등)도 기록이 버려지지 않게 기록 기준으로 만든다
        Player player = playerResolver.findOrCreate(dto.getPlayerId(), dto.getName());
        

        return batterStatRepository.findByPlayerKboPlayerIdAndSeason(playerId, dto.getSeason())
            .map(existing -> {
                    existing.updateBatterStat(player, dto.getGame(), dto.getPa(), dto.getAb(), dto.getRun(), dto.getHit(),
                        dto.getDouble2b(), dto.getTriple3b(), dto.getHr(), dto.getTb(), dto.getRbi(), dto.getSac(), dto.getSf(), dto.getBb(),
                        dto.getIbb(), dto.getHbp(), dto.getSo(), dto.getGdp(), dto.getSlg(), dto.getObp(), dto.getOps(), dto.getAvg());
                // 서비스 내부 호출이라 @Transactional이 적용되지 않아 변경 감지가 동작하지 않는다 → 명시적으로 저장
                return batterStatRepository.save(existing);
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
                    .avg(dto.getAvg())
                    .build()
            ));
    }
    
 
    /**
     * 주루 기록(도루·도루 실패)을 이미 저장된 타자 기록에 반영한다.
     * 타석 없이 대주자로만 나온 선수처럼 타자 기록이 없으면 false.
     */
    public boolean updateRunning(RunnerStatDto dto, Integer season){
        String playerId = String.valueOf(dto.getPlayerId());
        return batterStatRepository.findByPlayerKboPlayerIdAndSeason(playerId, season)
            .map(existing -> {
                existing.updateRunning(
                    dto.getSb() != null ? dto.getSb() : 0,
                    dto.getCs() != null ? dto.getCs() : 0);
                batterStatRepository.save(existing);
                return true;
            })
            .orElse(false);
    }
}
