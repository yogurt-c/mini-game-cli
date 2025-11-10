package io.yogurt.cli_mini_game.game.repository;

import io.yogurt.cli_mini_game.game.entity.GameType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameTypeRepository extends JpaRepository<GameType, Long> {

    Optional<GameType> findByCode(String code);
}