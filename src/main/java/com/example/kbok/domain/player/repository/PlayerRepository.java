package com.example.kbok.domain.player.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kbok.domain.player.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long>{
    Optional<Player> findByKboPlayerId(String kboPlayerId);
}
