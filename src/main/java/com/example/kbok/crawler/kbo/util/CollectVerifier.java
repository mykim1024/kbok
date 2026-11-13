package com.example.kbok.crawler.kbo.util;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * 수집 인원이 맞는지 검증한다. 틀려도 수집은 멈추지 않고 WARN만 남긴다.
 *
 * 배경: 투수 기본기록이 2페이지를 못 가져와 5개 팀에서 12명이 조용히 빠져 있었다.
 * 로그상으로는 "수집 완료"라 아무도 몰랐고, 세부기록 인원과 우연히 비교하다 발견했다.
 * 같은 종류의 누락을 자동으로 잡기 위해 세 가지로 검증한다.
 *
 * 1. 전체 건수: 페이지에 전체 건수가 표시되면 수집 인원과 비교
 * 2. 페이지 범위: 한 페이지 N명씩이면, 마지막 페이지를 뺀 나머지는 꽉 차 있어야 한다.
 *    → 수집 인원은 (페이지 수 - 1) × N 초과, 페이지 수 × N 이하
 * 3. 출처 간 비교: 같은 팀·같은 선수 집단을 여러 페이지에서 가져오면 인원이 같아야 한다.
 */
@Slf4j
public final class CollectVerifier {

    private CollectVerifier() {
    }

    public static boolean verifyTotal(String source, String teamCode, int collected, int expected) {
        if (collected == expected) {
            return true;
        }
        log.warn("[수집검증] {} {} 전체 건수 불일치 - 사이트 {}명, 수집 {}명", teamCode, source, expected, collected);
        return false;
    }

    public static boolean verifyPageRange(String source, String teamCode, int collected, int maxPage, int pageSize) {
        int min = (maxPage - 1) * pageSize + 1;
        int max = maxPage * pageSize;
        if (collected >= min && collected <= max) {
            return true;
        }
        log.warn("[수집검증] {} {} 페이지 수와 인원이 안 맞음 - {}페이지면 {}~{}명이어야 하는데 {}명 (페이지 누락 또는 중복 의심)",
            teamCode, source, maxPage, min, max, collected);
        return false;
    }

    public static boolean verifySame(String target, String teamCode, Map<String, Integer> counts) {
        if (counts.values().stream().distinct().count() <= 1) {
            return true;
        }
        String detail = counts.entrySet().stream()
            .map(e -> e.getKey() + " " + e.getValue() + "명")
            .collect(Collectors.joining(", "));
        log.warn("[수집검증] {} {} 출처별 인원 불일치 - {}", teamCode, target, detail);
        return false;
    }
}
