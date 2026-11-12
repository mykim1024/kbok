package com.example.kbok.domain.player.util;

/**
 * KBO 이닝 표기("150 2/3")를 아웃카운트 정수로 바꾼다.
 * 소수 이닝(150.67)은 반올림 오차가 생기지만, 아웃카운트는 정수라 계산이 정확하다.
 *   "150 2/3" → 452,  "150" → 450,  "2/3" → 2,  "-" / 빈 값 → 0
 */
public final class InningsConverter {

    private InningsConverter() {
    }

    public static int toOuts(String inningsPitched) {
        if (inningsPitched == null || inningsPitched.isBlank()) {
            return 0;
        }

        int outs = 0;
        for (String token : inningsPitched.trim().split("\\s+")) {
            try {
                if (token.contains("/")) {
                    outs += Integer.parseInt(token.split("/")[0]);   // "2/3" → 2아웃
                } else {
                    outs += Integer.parseInt(token) * 3;             // "150" → 450아웃
                }
            } catch (NumberFormatException e) {
                // "-" 같은 표기는 0으로 처리
            }
        }
        return outs;
    }
}
