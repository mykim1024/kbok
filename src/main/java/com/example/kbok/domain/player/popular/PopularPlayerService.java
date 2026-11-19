package com.example.kbok.domain.player.popular;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kbok.domain.player.dto.PlayerSummary;
import com.example.kbok.domain.player.ranking.RankCalculator;
import com.example.kbok.domain.player.repository.PlayerQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PopularPlayerService {
    
    private static final int TOP_SIZE = 10;
    private static final int FETCH_SIZE = 15;

    private final PlayerViewCounter playerViewCounter;
    private final PlayerQueryRepository playerQueryRepository;

    public List<PopolarPlayer> findTodayTop(){
        List<ViewCount> viewCounts = playerViewCounter.findTodayTop(FETCH_SIZE);

        Set<String> ids = viewCounts.stream()
            .map(ViewCount::kboPlayerId)
            .collect(Collectors.toSet());
        
        Map<String, PlayerSummary> players = playerQueryRepository.findSummarise(ids).stream()
            .collect(Collectors.toMap(PlayerSummary::kboPlayerId, Function.identity()));

        List<ViewCount> top =  viewCounts.stream()
            .filter(v -> {
                boolean exists = players.containsKey(v.kboPlayerId());
                if(!exists){
                    log.warn("DB에 없는 선수 건너뜀: kboPlayerId={}", v.kboPlayerId());
                }
                return exists;
            })
            .limit(TOP_SIZE)
            .toList();

        List<Long> allViews = top.stream().map(ViewCount::views).toList();
        return top.stream()
            .map(v -> {
                PlayerSummary p = players.get(v.kboPlayerId());
                int rank = RankCalculator.rankOf(v.views(), allViews).rank();
                return new PopolarPlayer(rank, v.type(), v.kboPlayerId(), p.name(), p.team(), v.views());
            })
            .toList();
    }
}
