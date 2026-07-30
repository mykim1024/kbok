package com.example.kbok.domain.player.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kbok.domain.player.entity.BatterStat;

public interface BatterStatRepository extends JpaRepository<BatterStat, Long>{
    Optional<BatterStat> findByPlayerKboPlayerIdAndSeason(String kboPlayerId, Integer season);
}
