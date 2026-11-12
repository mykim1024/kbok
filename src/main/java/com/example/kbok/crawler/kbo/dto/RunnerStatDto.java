package com.example.kbok.crawler.kbo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/** KBO 기록실 주루기록 한 줄 */
@Data
@NoArgsConstructor
public class RunnerStatDto {
    private Long playerId;

    private String name;
    private String team;

    private Integer sb;     // 도루
    private Integer cs;     // 도루 실패
}
