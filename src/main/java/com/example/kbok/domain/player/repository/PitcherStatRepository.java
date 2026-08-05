package com.example.kbok.domain.player.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kbok.domain.player.entity.PitcherStat;

public interface PitcherStatRepository extends JpaRepository<PitcherStat, Long>{

    Optional<PitcherStat> findByPlayerKboPlayerIdAndSeason(String kboPlayerId, Integer season);
}
