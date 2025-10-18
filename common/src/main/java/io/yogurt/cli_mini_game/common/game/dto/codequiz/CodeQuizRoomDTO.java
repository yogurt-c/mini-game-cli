package io.yogurt.cli_mini_game.common.game.dto.codequiz;

import java.time.LocalDateTime;
import java.util.List;

public record CodeQuizRoomDTO(
    String roomId,
    String roomName,
    String status,
    int maxPlayers,
    int currentPlayers,
    List<String> playerNicknames,
    LocalDateTime createdAt
) {
}
