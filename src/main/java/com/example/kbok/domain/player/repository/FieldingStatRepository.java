package com.example.kbok.domain.player.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kbok.domain.player.entity.FieldingStat;

public interface FieldingStatRepository extends JpaRepository<FieldingStat, Long> {
    Optional<FieldingStat> findByPlayerKboPlayerIdAndSeasonAndPosition(String kboPlayerId, Integer season, String position);
}
