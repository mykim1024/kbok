package com.example.kbok.domain.player.repository;

import static com.example.kbok.domain.player.entity.QBatterStat.batterStat;
import static com.example.kbok.domain.player.entity.QPlayer.player;
import static com.example.kbok.domain.team.entity.QTeam.team;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.example.kbok.domain.player.dto.BatterListItem;
import com.example.kbok.domain.player.dto.BatterSearchCondition;
import com.example.kbok.domain.player.dto.BatterSort;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BatterStatQueryRepository {
    
    private final JPAQueryFactory queryFactory;

    public Page<BatterListItem> search(BatterSearchCondition cond, Pageable pageable){
        List<BatterListItem> content = queryFactory
            .select(Projections.constructor(BatterListItem.class,
                player.name,
                team.name,
                batterStat.pa,
                batterStat.homeRuns,
                batterStat.avg,
                batterStat.ops
            ))
            .from(batterStat)
            .join(batterStat.player, player)
            .join(batterStat.team, team)
            .where(
                batterStat.season.eq(cond.season()),
                teamEq(cond.team()),
                nameContains(cond.name()),
                qualifiedOnly(cond.qualified())
            )
            .orderBy(
                qualifiedFirst(),
                sortOrder(cond.sort()),
                batterStat.id.asc()
            )
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory
            .select(batterStat.count())
            .from(batterStat)
            .join(batterStat.player, player)
            .join(batterStat.team, team)
            .where(
                batterStat.season.eq(cond.season()),
                teamEq(cond.team()),
                nameContains(cond.name()),
                qualifiedOnly(cond.qualified())
            );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    //규정 타석: 144경기 * 3.1 = 446.4
    private static final int QUALIFIED_PA = 446;

    private BooleanExpression teamEq(String teamName){
        return StringUtils.hasText(teamName) ? team.name.eq(teamName) : null;
    }

    private BooleanExpression nameContains(String name){
        return StringUtils.hasText(name) ? player.name.contains(name) : null;
    }

    private BooleanExpression qualifiedOnly(Boolean qualified){
        return Boolean.TRUE.equals(qualified) ? batterStat.pa.goe(QUALIFIED_PA) : null;
    }

    private OrderSpecifier<?> sortOrder(BatterSort sort){
        return switch (sort) {
            case OPS -> batterStat.ops.desc().nullsLast();
            case AVG -> batterStat.avg.desc().nullsLast();
            case HR -> batterStat.homeRuns.desc();
        };
    }

    private OrderSpecifier<Integer> qualifiedFirst(){
        NumberExpression<Integer> rank = new CaseBuilder()
            .when(batterStat.pa.goe(QUALIFIED_PA)).then(0)
            .otherwise(1);

        return rank.asc();
    }

}
