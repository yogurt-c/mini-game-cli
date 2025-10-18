package io.yogurt.cli_mini_game.common.game.dto.codequiz;

import java.util.List;

/**
 * 게임 시작 메시지 (서버 → 클라이언트)
 */
public record GameStartMessage(
    String messageType,      // "GAME_START"
    List<String> players,    // 참가자 닉네임 리스트
    int gameDurationMinutes  // 게임 시간 (분)
) {
    public GameStartMessage(List<String> players, int gameDurationMinutes) {
        this("GAME_START", players, gameDurationMinutes);
    }
}
