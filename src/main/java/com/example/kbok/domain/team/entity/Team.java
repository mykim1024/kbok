package com.example.kbok.domain.team.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "team")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String name;

    @Column(nullable = false, length = 20)
    private String fullName;

    @Column(nullable = false, length = 20)
    private String city;

    @Column(nullable = false)
    private Integer foundedYear;

    @Builder
    public Team(String name, String fullName, String city, Integer foundedYear) {
        this.name = name;
        this.fullName = fullName;
        this.city = city;
        this.foundedYear = foundedYear;
    }
}