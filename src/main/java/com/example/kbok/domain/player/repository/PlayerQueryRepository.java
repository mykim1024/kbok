package com.example.kbok.domain.player.repository;

import static com.example.kbok.domain.player.entity.QPlayer.player;
import static com.example.kbok.domain.team.entity.QTeam.team;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import com.example.kbok.domain.player.dto.PlayerSummary;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PlayerQueryRepository {
    
    private final JPAQueryFactory queryFactory;

    public List<PlayerSummary> findSummarise(Collection<String> kboPlayerIds){
        if(kboPlayerIds.isEmpty()){
            return List.of();
        }

        return queryFactory
                    .select(Projections.constructor(PlayerSummary.class,
                        player.kboPlayerId, player.name, team.name))
                    .from(player)
                    .leftJoin(player.team, team)
                    .where(player.kboPlayerId.in(kboPlayerIds))
                    .fetch();
    }
}
