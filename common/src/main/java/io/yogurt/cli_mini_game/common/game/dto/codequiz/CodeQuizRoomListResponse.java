package io.yogurt.cli_mini_game.common.game.dto.codequiz;

import java.util.List;

public record CodeQuizRoomListResponse(
    List<CodeQuizRoomDTO> rooms
) {
}
