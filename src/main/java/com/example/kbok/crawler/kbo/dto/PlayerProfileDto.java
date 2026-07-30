package com.example.kbok.crawler.kbo.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerProfileDto {

    private Long kboPlayerId;
    private String backNumber;
    private String name;
    private String teamName;
    private String position;
    private LocalDate birthDate;
    private Integer height;
    private Integer weight;
    private String career; // 학교/프로팀 이력 원본 텍스트, 파싱은 나중에
}