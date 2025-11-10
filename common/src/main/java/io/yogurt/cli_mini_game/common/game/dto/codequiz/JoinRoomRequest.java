package io.yogurt.cli_mini_game.common.game.dto.codequiz;

public record JoinRoomRequest(
    String roomId,
    String nickname
) {
}
