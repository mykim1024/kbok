package com.example.kbok.crawler.kbo.mapper;

import org.springframework.stereotype.Component;

import com.example.kbok.domain.player.entity.Player;
import com.example.kbok.domain.player.repository.PlayerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 기록에 나온 선수를 찾고, 없으면 ID·이름만으로 만든다.
 *
 * 선수 명단은 "현재 등록 선수" 기준이라, 시즌 중 방출·이적·은퇴한 선수는 명단에 없지만 기록은 있다.
 * 과거 시즌을 수집할 때는 이런 선수가 훨씬 많아진다.
 * 그래서 선수는 명단이 아니라 "기록에 나온 KBO 선수 ID"를 기준으로 존재하게 한다.
 *
 * - 현재 소속은 알 수 없으므로 team은 비워 둔다. (그 시즌 소속은 기록 테이블의 team_id가 담당)
 * - 현재 명단에 없는 선수이므로 비활성으로 둔다. 이후 명단에 나타나면 PlayerProfileMapper가 다시 활성화한다.
 * - 생년월일·포지션 등 프로필은 선수 상세 페이지에서 따로 채운다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PlayerResolver {

    private final PlayerRepository playerRepository;

    public Player findOrCreate(Long kboPlayerId, String name) {
        if (kboPlayerId == null) {
            throw new IllegalStateException("선수 ID 없음 - name=" + name);
        }
        String id = String.valueOf(kboPlayerId);

        return playerRepository.findByKboPlayerId(id)
            .orElseGet(() -> {
                Player created = Player.builder()
                    .kboPlayerId(id)
                    .name(name)
                    .build();
                created.deactivate();
                log.info("[Player] 명단에 없는 선수를 기록 기준으로 생성 - id={}, name={}", id, name);
                return playerRepository.save(created);
            });
    }
}
