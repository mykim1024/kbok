package com.example.kbok.domain.player.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.kbok.config.CacheConfig;
import com.example.kbok.domain.player.dto.BatterDetailResponse;
import com.example.kbok.domain.player.dto.BatterDetailRow;
import com.example.kbok.domain.player.dto.BatterListItem;
import com.example.kbok.domain.player.dto.BatterSearchCondition;
import com.example.kbok.domain.player.dto.BatterStatValues;
import com.example.kbok.domain.player.dto.StatInterpretation;
import com.example.kbok.domain.player.ranking.BattingStandard;
import com.example.kbok.domain.player.ranking.LeagueBattingTotals;
import com.example.kbok.domain.player.ranking.Rank;
import com.example.kbok.domain.player.ranking.RankCalculator;
import com.example.kbok.domain.player.repository.BatterStatQueryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BatterQueryService {
    
    private final BatterStatQueryRepository batterStatQueryRepository;

    //목록 조회
    public Page<BatterListItem> search(BatterSearchCondition cond, Pageable pageable){
        return batterStatQueryRepository.search(cond, pageable);
    }

    //상세조회
    @Cacheable(cacheNames = CacheConfig.BATTER_DETAIL, key = "#kboPlayerId + ':' + #season")
    public BatterDetailResponse findDetail(String kboPlayerId, Integer season) {
        BatterDetailRow row = batterStatQueryRepository.findDetail(kboPlayerId, season);
        if (row == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 시즌 기록이 없습니다");
        }

        BatterStatValues target = row.values();
        List<BatterStatValues> all = batterStatQueryRepository.findSeasonValues(season);
        List<BatterStatValues> qualifiedBatters = all.stream()
                .filter(BatterStatValues::qualified)
                .toList();
        LeagueBattingTotals league = batterStatQueryRepository.findLeagueTotals(season);
        boolean qualified = target.qualified();

        List<StatInterpretation> stats = List.of(
                interpret("타율", target.avg(), valuesOf(qualifiedBatters, BatterStatValues::avg),
                        qualified, league.avg()),
                interpret("출루율", target.obp(), valuesOf(qualifiedBatters, BatterStatValues::obp),
                        qualified, league.obp()),
                interpret("장타율", target.slg(), valuesOf(qualifiedBatters, BatterStatValues::slg),
                        qualified, league.slg()),
                interpret("OPS", target.ops(), valuesOf(qualifiedBatters, BatterStatValues::ops),
                        qualified, league.ops()),
                interpret("홈런", target.homeRuns(), valuesOf(all, BatterStatValues::homeRuns),
                        isPositive(target.homeRuns()), null)
        );

        return new BatterDetailResponse(
                row.kboPlayerId(), row.name(), row.team(), row.position(),
                season, row.pa(), qualified, BattingStandard.QUALIFIED_PA, stats);
    }

    private <T> List<T> valuesOf(List<BatterStatValues> rows, Function<BatterStatValues, T> getter) {
        return rows.stream().map(getter).toList();
    }

    private <T extends Number & Comparable<T>> StatInterpretation interpret(
        String label, T value, List<T> comparisons, boolean rankable, BigDecimal leagueAverage) {
        if (!rankable || value == null) {
            return new StatInterpretation(label, value, null, null, null, leagueAverage);
        }
        Rank rank = RankCalculator.rankOf(value, comparisons);
        return new StatInterpretation(label, value, rank.rank(), rank.total(), rank.topPercent(), leagueAverage);
    }

    private boolean isPositive(Integer value) {
        return value != null && value > 0;
    }
}
