package io.yogurt.cli_mini_game.common.game.dto.codequiz;

/**
 * 게임 시작 요청 (클라이언트 → 서버)
 */
public record StartGameRequest(
    String roomId
) {
}
