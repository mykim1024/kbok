package com.example.kbok.domain.player.repository;

import static com.example.kbok.domain.player.entity.QPitcherStat.pitcherStat;
import static com.example.kbok.domain.player.entity.QPlayer.player;
import static com.example.kbok.domain.team.entity.QTeam.team;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.kbok.domain.player.dto.PitcherDetailRow;
import com.example.kbok.domain.player.dto.PitcherListItem;
import com.example.kbok.domain.player.dto.PitcherRole;
import com.example.kbok.domain.player.dto.PitcherSearchCondition;
import com.example.kbok.domain.player.dto.PitcherSort;
import com.example.kbok.domain.player.dto.PitcherStatValues;
import com.example.kbok.domain.player.entity.PitcherStat;
import com.example.kbok.domain.player.ranking.LeaguePitchingTotals;
import com.example.kbok.domain.player.ranking.PitchingStandard;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PitcherStatQueryRepository {
    
    private final JPAQueryFactory queryFactory;

    public Page<PitcherListItem> search(PitcherSearchCondition cond, Pageable pageable){
        List<PitcherListItem> content = queryFactory
            .select(Projections.constructor(PitcherListItem.class,
                player.kboPlayerId, player.name, team.name,
                pitcherStat.games, pitcherStat.gamesStarted, pitcherStat.inningsPitched,
                pitcherStat.wins, pitcherStat.losses, pitcherStat.saves, pitcherStat.holds,
                pitcherStat.so, pitcherStat.era, pitcherStat.whip))
            .from(pitcherStat)
            .join(pitcherStat.player, player)
            .join(pitcherStat.team, team)
            .where(
                pitcherStat.season.eq(cond.season()),
                teamEq(cond.team()),
                nameContains(cond.name()),
                roleEq(cond.role()),
                qualifiedOnly(cond.qualified())
            )
            .orderBy(orders(cond.sort()))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(pitcherStat.count())
            .from(pitcherStat)
            .join(pitcherStat.player, player)
            .join(pitcherStat.team, team)
            .where(
                pitcherStat.season.eq(cond.season()),
                teamEq(cond.team()),
                nameContains(cond.name()),
                roleEq(cond.role()),
                qualifiedOnly(cond.qualified())
            );
        
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression teamEq(String teamName){
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression nameContains(String name){
        return StringUtils.hasText(name) ? player.name.contains(name) : null;
    }

    private BooleanExpression roleEq(PitcherRole role){
        if (role == null){
            return null;
        }

        BooleanExpression starter = pitcherStat.gamesStarted.multiply(2).goe(pitcherStat.games);
        return role == PitcherRole.STARTER ? starter : starter.not();
    }

    private BooleanExpression qualifiedOnly(Boolean qualified){
        return Boolean.TRUE.equals(qualified) ? pitcherStat.inningsOuts.goe(PitchingStandard.QUALIFIED_OUTS) : null;
    }

    private OrderSpecifier<?>[] orders(PitcherSort sort){
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        if(sort.isRateStat()){
            orders.add(qualifiedFirst());
        }
        orders.add(sortOrder(sort));
        orders.add(pitcherStat.id.asc());
        return orders.toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<Integer> qualifiedFirst(){
        return new CaseBuilder()
            .when(pitcherStat.inningsOuts.goe(PitchingStandard.QUALIFIED_OUTS)).then(0)
            .otherwise(1)
            .asc();
    }

    private OrderSpecifier<?> sortOrder(PitcherSort sort) {
        return switch (sort) {
            case ERA -> pitcherStat.era.asc().nullsLast();
            case WHIP -> pitcherStat.whip.asc().nullsLast();
            case WINS -> pitcherStat.wins.desc();
            case SO -> pitcherStat.so.desc();
            case SAVES -> pitcherStat.saves.desc();
            case HOLDS -> pitcherStat.holds.desc();
        };
    }

    public LeaguePitchingTotals findLeagueTotals(Integer season){
        return queryFactory
            .select(Projections.constructor(LeaguePitchingTotals.class,
                pitcherStat.earnedRuns.sum().longValue(),
                pitcherStat.inningsOuts.sum().longValue(),
                pitcherStat.bb.sum().longValue(),
                pitcherStat.hits.sum().longValue()))
            .from(pitcherStat)
            .where(pitcherStat.season.eq(season))
            .fetchOne();
    }

    public List<PitcherStatValues> findStatValues(Integer season){
        return queryFactory
            .select(Projections.constructor(PitcherStatValues.class,
                pitcherStat.inningsOuts, pitcherStat.era, pitcherStat.whip,
                pitcherStat.wins, pitcherStat.so, pitcherStat.saves, pitcherStat.holds))
            .from(pitcherStat)
            .where(pitcherStat.season.eq(season))
            .fetch();
    }

    public PitcherDetailRow findDetail(String kboPlayerId, Integer season){
        return queryFactory
            .select(Projections.constructor(PitcherDetailRow.class, 
                player.kboPlayerId, player.name, team.name,
                pitcherStat.games, pitcherStat.gamesStarted, pitcherStat.inningsPitched,
                pitcherStat.inningsOuts, pitcherStat.era, pitcherStat.whip,
                pitcherStat.wins, pitcherStat.so, pitcherStat.saves, pitcherStat.holds))
            .from(pitcherStat)
            .join(pitcherStat.player, player)
            .join(pitcherStat.team, team)
            .where(
                player.kboPlayerId.eq(kboPlayerId),
                pitcherStat.season.eq(season)
            )
            .fetchOne();
    }
}
