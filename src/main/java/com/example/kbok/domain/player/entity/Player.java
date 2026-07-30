package com.example.kbok.domain.player.entity;

import java.time.LocalDate;

import com.example.kbok.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "player")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String kboPlayerId;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(length = 10)
    private String position;

    private LocalDate birthDate;

    private Integer height;

    private Integer weight;

    @Column(length = 100)
    private String school;

    @Column(length = 5)
    private String backNumber;

    @Builder
    public Player(String kboPlayerId, String name, Team team, String position, LocalDate birthDate, Integer height, Integer weight, String school, String backNumber) {
        this.kboPlayerId = kboPlayerId;
        this.name = name;
        this.team = team;
        this.position = position;
        this.birthDate = birthDate;
        this.height = height;
        this.weight = weight;
        this.school = school;
        this.backNumber = backNumber;
    }

    public void updateProfile(String position, LocalDate birthDate, 
            Integer height, Integer weight, String school, String backNumber, Team team){
        this.position = position;
        this.birthDate = birthDate;
        this.weight = weight;
        this.school = school;
        this.backNumber = backNumber;
        this.team = team;
    }
}