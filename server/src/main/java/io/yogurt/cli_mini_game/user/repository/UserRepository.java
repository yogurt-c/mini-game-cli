package io.yogurt.cli_mini_game.user.repository;

import io.yogurt.cli_mini_game.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    User findByUsername(String username);
}
