package com.example.kbok.domain.player.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.kbok.domain.player.dto.PitcherDetailResponse;
import com.example.kbok.domain.player.dto.PitcherDetailRow;
import com.example.kbok.domain.player.dto.PitcherListItem;
import com.example.kbok.domain.player.dto.PitcherRole;
import com.example.kbok.domain.player.dto.PitcherSearchCondition;
import com.example.kbok.domain.player.dto.PitcherStatValues;
import com.example.kbok.domain.player.dto.StatInterpretation;
import com.example.kbok.domain.player.ranking.LeaguePitchingTotals;
import com.example.kbok.domain.player.ranking.PitchingStandard;
import com.example.kbok.domain.player.ranking.Rank;
import com.example.kbok.domain.player.ranking.RankCalculator;
import com.example.kbok.domain.player.ranking.RankDirection;
import com.example.kbok.domain.player.repository.PitcherStatQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PitcherQueryService {
    private final PitcherStatQueryRepository pitcherStatQueryRepository;

    //목록 조회
    public Page<PitcherListItem> search(PitcherSearchCondition cond, Pageable pageable){
        return pitcherStatQueryRepository.search(cond, pageable);
    }

    //상세 조회
    public PitcherDetailResponse findDetail(String kboPlayerId, Integer season){
        PitcherDetailRow row = pitcherStatQueryRepository.findDetail(kboPlayerId, season);
        if(row == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 시즌 기록이 없습니다.");
        }

        PitcherStatValues target = row.values();
        List<PitcherStatValues> all = pitcherStatQueryRepository.findStatValues(season);
        List<PitcherStatValues> qualifiedPitchers = all.stream()
            .filter(PitcherStatValues::qualified)
            .toList();
        LeaguePitchingTotals league = pitcherStatQueryRepository.findLeagueTotals(season);
        boolean qualified = target.qualified();

        List<StatInterpretation> stats = List.of(
            interpret("평균자책점", target.era(), valuesOf(qualifiedPitchers, PitcherStatValues::era),
                    RankDirection.LOWER_IS_BETTER, qualified, league.era()),
            interpret("WHIP", target.whip(), valuesOf(qualifiedPitchers, PitcherStatValues::whip),
                    RankDirection.LOWER_IS_BETTER, qualified, league.whip()),
            interpret("승", target.wins(), valuesOf(all, PitcherStatValues::wins),
                    RankDirection.HIGHER_IS_BETTER, isPositive(target.wins()), null),
            interpret("탈삼진", target.so(), valuesOf(all, PitcherStatValues::so),
                    RankDirection.HIGHER_IS_BETTER, isPositive(target.so()), null),
            interpret("세이브", target.saves(), valuesOf(all, PitcherStatValues::saves),
                    RankDirection.HIGHER_IS_BETTER, isPositive(target.saves()), null),
            interpret("홀드", target.holds(), valuesOf(all, PitcherStatValues::holds),
                    RankDirection.HIGHER_IS_BETTER, isPositive(target.holds()), null)
        );

        return new PitcherDetailResponse(
            row.kboPlayerId(), row.name(), row.team(),
            PitcherRole.of(row.games(), row.gamesStarted()),
            season, row.games(), row.gamesStarted(), row.inningsPitched(), row.inningsPerGame(),
            qualified, PitchingStandard.QUALIFIED_OUTS / 3, stats);
    }
    
    private <T extends Number & Comparable<T>> StatInterpretation interpret(
        String label, T value, List<T> comparisons,
        RankDirection direction, boolean rankable, BigDecimal leagueAverage) {
        if (!rankable || value == null) {
            return new StatInterpretation(label, value, null, null, null, leagueAverage);
        }
        Rank rank = RankCalculator.rankOf(value, comparisons, direction);
        return new StatInterpretation(label, value, rank.rank(), rank.total(), rank.topPercent(), leagueAverage);
    }

    private boolean isPositive(Integer value) {
        return value != null && value > 0;
    }

    private <T> List<T> valuesOf(List<PitcherStatValues> rows, Function<PitcherStatValues, T> getter) {
        return rows.stream().map(getter).toList();
    }

}
