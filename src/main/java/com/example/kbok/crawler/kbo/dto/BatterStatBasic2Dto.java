package com.example.kbok.crawler.kbo.dto;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BatterStatBasic2Dto {
    private Long playerId;

    private String name;
    private String team;

    private Integer bb;          // 볼넷
    private Integer ibb;         // 고의4구
    private Integer hbp;         // 사구
    private Integer so;          // 삼진
    private Integer gdp;         // 병살타
    private BigDecimal slg;         // 장타율
    private BigDecimal obp;         // 출루율
    private BigDecimal ops;         // OPS
}
